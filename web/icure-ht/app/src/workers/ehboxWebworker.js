import * as fhcApi from '../elements/fhc-api/fhcApi'
import * as iccApi from 'icc-api/dist/icc-api/iccApi'
import * as iccXApi from 'icc-api/dist/icc-x-api/index'

onmessage = function(e){
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

        const ehboxApi          = new fhcApi.fhcEhboxcontrollerApi(fhcHost, fhcHeaders)

        const docApi            = new iccApi.iccDocumentApi(iccHost, iccHeaders)
        const msgApi            = new iccApi.iccMessageApi(iccHost, iccHeaders)
        const beResultApi       = new iccApi.iccBeresultimportApi(iccHost, iccHeaders)

        const iccHcpartyApi     = new iccApi.iccHcpartyApi(iccHost, iccHeaders)
        const iccPatientApi     = new iccApi.iccPatientApi(iccHost, iccHeaders)
        const iccCryptoXApi     = new iccXApi.IccCryptoXApi(iccHost, iccHeaders, iccHcpartyApi)

        //Avoid the hit to the local storage to load the key pair
        iccCryptoXApi.cacheKeyPair(e.data.keyPair, user.healthcarePartyId)

        const docxApi           = new iccXApi.IccDocumentXApi(iccHost, iccHeaders, iccCryptoXApi)

        var nbOfEhboxMessage = 0

        ehboxApi.loadMessagesUsingGET(keystoreId, tokenId, ehpassword, boxId, 100).then(messages => {
            messages.map(message => {
                  ehboxApi.getFullMessageUsingGET(keystoreId, tokenId, ehpassword, boxId, message.id).then(fullMessage => {
                      console.log(fullMessage)
                      msgApi.findMessagesByTransportGuid(boxId+":"+message.id, null, null, 1000).then(existingMess => {

                        if(existingMess.rows.length > 0){
                            console.log("Message found")
                        }else{
                            console.log('Message not found')
                            nbOfEhboxMessage ++

                            let newMessage = {
                                fromAddress:            fullMessage.sender.lastName+' '+fullMessage.sender.firstName,
                                subject:                fullMessage.document.title,
                                metas:                  fullMessage.customMetas,
                                toAddresses:            ["INBOX"],
                                fromHealthcarePartyId:  ""
                            }

                            msgApi.createMessage(newMessage).then(createdMessage => {
                              fullMessage.annex.map(a => {
                                  docxApi.newInstance(user, createdMessage, {documentType: 'result', mainUti: docxApi.uti(a.mimeType), name: a.title}).then(d => {
                                      console.log(d)
                                      docApi.createDocument(d).then(createdDocument => {
                                         console.log(createdDocument)
                                      })
                                  })
                              })
                            })
                        }

                        postMessage({message: "You have "+nbOfEhboxMessage+" new messages into your ehbox"})

                      })
                  })
                })
        })
    }
};

