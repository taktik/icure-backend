import * as fhcApi from 'fhc-api/dist/fhcApi'
import * as iccApi from 'icc-api/dist/icc-api/iccApi'
import * as iccXApi from 'icc-api/dist/icc-x-api/index'
import {UtilsClass} from "icc-api/dist/icc-x-api/crypto/utils"

import moment from 'moment/src/moment'

onmessage = e => {
    if(e.data.action === "loadEhboxMessage"){
        const iccHost           = e.data.iccHost
        const iccHeaders        = JSON.parse(e.data.iccHeaders)

        const fhcHost           = e.data.fhcHost
        const fhcHeaders        = JSON.parse(e.data.fhcHeaders)
        const hcpartyBaseApi    = e.data.hcpartyBaseApi

        const tokenId           = e.data.tokenId
        const keystoreId        = e.data.keystoreId
        const user              = e.data.user
        const ehpassword        = e.data.ehpassword
        const boxId             = e.data.boxId
		const alternateKeystores= e.data.alternateKeystores

        const ehboxApi          = new fhcApi.fhcEhboxcontrollerApi(fhcHost, fhcHeaders)

        const docApi            = new iccApi.iccDocumentApi(iccHost, iccHeaders)
        const msgApi            = new iccApi.iccMessageApi(iccHost, iccHeaders)
        const beResultApi       = new iccApi.iccBeresultimportApi(iccHost, iccHeaders)

        const iccHcpartyApi     = new iccApi.iccHcpartyApi(iccHost, iccHeaders)
        const iccPatientApi     = new iccApi.iccPatientApi(iccHost, iccHeaders)
        const iccCryptoXApi     = new iccXApi.IccCryptoXApi(iccHost, iccHeaders, iccHcpartyApi)

        const iccUtils          = new UtilsClass()

        //Avoid the hit to the local storage to load the key pair
        iccCryptoXApi.cacheKeyPair(e.data.keyPair, user.healthcarePartyId)

        const docxApi           = new iccXApi.IccDocumentXApi(iccHost, iccHeaders, iccCryptoXApi)
        const iccMessageXApi    = new iccXApi.IccMessageXApi(iccHost, iccHeaders, iccCryptoXApi)

        const treatMessage =  (message) => ehboxApi.getFullMessageUsingGET(keystoreId, tokenId, ehpassword, boxId, message.id)
            .then(fullMessage => msgApi.findMessagesByTransportGuid(boxId+":"+message.id, null, null, 1).then(existingMess => [fullMessage, existingMess]))
            .then(([fullMessage, existingMess]) => {
                console.log(fullMessage)
                if(existingMess.rows.length > 0){
                    console.log("Message found")

                    const existingMessage = existingMess.rows[0]

                    if(existingMessage.created !== null && existingMessage.created < (Date.now() - (24 * 3600000))){
                        return fullMessage.id
                    }
                } else {
                    console.log('Message not found')

                    let createdDate = moment(fullMessage.publicationDateTime, "YYYYMMDD").valueOf()
                    let receivedDate = new Date().getTime()

                    let newMessage = {
                        created:                createdDate,
                        fromAddress:            fullMessage.sender.lastName+' '+fullMessage.sender.firstName,
                        subject:                fullMessage.document && fullMessage.document.title || fullMessage.errorCode,
                        metas:                  fullMessage.customMetas,
                        toAddresses:            [boxId],
                        fromHealthcarePartyId:  "",
                        transportGuid:          boxId+":"+fullMessage.id,
                        received:               receivedDate

                    }

                    return iccMessageXApi.newInstance(user, newMessage)
                        .then(messageInstance => msgApi.createMessage(messageInstance))
                        .then(createdMessage => {
                            console.log(createdMessage)
                            Promise.all((fullMessage.document ? [fullMessage.document] : []).concat(fullMessage.annex || []).map(a => a &&
                                docxApi.newInstance(user, createdMessage, {
                                    documentLocation:   (fullMessage.document && a === fullMessage.document.content) ? 'body' : 'annex',
                                    documentType:       'result', //Todo identify message and set type accordingly
                                    mainUti:            docxApi.uti(a.mimeType, a.filename && a.filename.replace(/.+\.(.+)/,'$1')),
                                    name:               a.title
                                })
                                    .then(d => docApi.createDocument(d))
                                    .then(createdDocument => {
                                        let byteContent = iccUtils.base64toArrayBuffer(a.content)
                                        return [createdDocument, byteContent]
                                    })
                                    .then(([createdDocument, byteContent]) => docApi.setAttachment(createdDocument.id, null, byteContent))
                            ))
                        })
                        .then(() => null) //DO NOT RETURN A MESSAGE ID TO BE DELETED
                }
            })

        ehboxApi.loadMessagesUsingPOST(keystoreId, tokenId, ehpassword, boxId, 100, alternateKeystores).then(messages => {
            let p = Promise.resolve([])
            messages.forEach(m => {
                p = p.then(acc => treatMessage(m).then(id => id ? acc.concat([id]) : acc))
            })
            return p
        }).then(toBeDeletedIds =>
            Promise.all(toBeDeletedIds.map(id => ehboxApi.moveMessagesUsingPOST(keystoreId, tokenId, ehpassword, [id], boxId, "BININBOX")))
        )
    }
};

