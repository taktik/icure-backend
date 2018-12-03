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
		const fhcCrypto			= e.data.fhcCrypto

        const tokenId           = e.data.tokenId
        const keystoreId        = e.data.keystoreId
        const user              = e.data.user
        const ehpassword        = e.data.ehpassword
        const boxIds             = e.data.boxId
		const alternateKeystores= e.data.alternateKeystores

        const ehboxApi          = new fhcApi.fhcEhboxcontrollerApi(fhcHost, fhcHeaders)

        const docApi            = new iccApi.iccDocumentApi(iccHost, iccHeaders)
        const msgApi            = new iccApi.iccMessageApi(iccHost, iccHeaders)
        const beResultApi       = new iccApi.iccBeresultimportApi(iccHost, iccHeaders)


        const iccHcpartyApi     = new iccApi.iccHcpartyApi(iccHost, iccHeaders)
        const iccPatientApi     = new iccApi.iccPatientApi(iccHost, iccHeaders)
		const iccContactApi		= new iccApi.iccContactApi(iccHost, iccHeaders)
		const iccFormApi		= new iccApi.iccFormApi(iccHost, iccHeaders)
        const iccCryptoXApi     = new iccXApi.IccCryptoXApi(iccHost, iccHeaders, iccHcpartyApi)
        const iccDocumentXApi   = new iccXApi.IccDocumentXApi(iccHost, iccHeaders, iccHcpartyApi)
		const iccContactXApi	= new iccXApi.IccContactXApi(iccHost, iccHeaders,iccCryptoXApi)
		const iccFormXApi		= new iccXApi.IccFormXApi(iccHost, iccHeaders,iccCryptoXApi)

        const iccUtils          = new UtilsClass()

        //Avoid the hit to the local storage to load the key pair
        iccCryptoXApi.cacheKeyPair(e.data.keyPair, user.healthcarePartyId)

        const docxApi           = new iccXApi.IccDocumentXApi(iccHost, iccHeaders, iccCryptoXApi)
        const iccMessageXApi    = new iccXApi.IccMessageXApi(iccHost, iccHeaders, iccCryptoXApi)

		const textType = (uti, utis) =>{
			return (uti && [uti] || []).concat(utis && utis.value || []).map(u => iccDocumentXApi.mimeType(u)).find(m => m === 'text/plain');
		}

		const assignAttachment = (messageId,docInfo,document) =>
			textType(document.mainUti, document.otherUtis)?
				iccPatientApi.findByNameBirthSsinAuto(user.healthcarePartyId, docInfo.lastName + " " + docInfo.firstName, null, null, 100, "asc").then(patients =>
					(patients && patients.rows && patients.rows.length === 1)?
						iccContactXApi.newInstance(user, patients.rows[0], {
						groupId : messageId,
						created: new Date().getTime() ,
						modified: new Date().getTime() ,
						author: user.healthcarePartyId,
						responsible: user.healthcarePartyId,
						openingDate: moment().format('YYYYMMDD') || '',
						closingDate: moment().format('YYYYMMDD') || '',
						encounterType: {type: docInfo.codes.type, version: docInfo.codes.version, code: docInfo.codes.code},
						descr: docInfo.labo,
						subContacts: []
					}).then(c =>
							iccContactApi.createContact(c).then(c =>
								iccFormXApi.newInstance(user, patients.rows[0], {contactId: c.id,descr: "Lab " + new Date().getTime(),}).then(f =>iccFormXApi.createForm(f)).then(f =>
									iccHcpartyApi.getHealthcareParty(user.healthcarePartyId).then(hcp =>
									beResultApi.doImport(document.id, user.healthcarePartyId, hcp.languages.find(e => !!e) || "en", docInfo.protocol, f.id, null, c)
									.then(c => {
										console.log("Contact id " + c.id);
										return {id:c.id,protocolId:docInfo.protocol}
									})
								)
							)
						)
					):null
				)
			:null

        const treatMessage =  (message,boxId) => ehboxApi.getFullMessageUsingGET(keystoreId, tokenId, ehpassword, boxId, message.id)
            .then(fullMessage => msgApi.findMessagesByTransportGuid(boxId+":"+message.id, null, null, 1).then(existingMess => [fullMessage, existingMess]))
            .then(([fullMessage, existingMess]) => {
                console.log(fullMessage)
                if (existingMess.rows.length > 0) {
                    console.log("Message found")

                    const existingMessage = existingMess.rows[0]

                    return (existingMessage.created !== null && existingMessage.created < (Date.now() - (24 * 3600000))) ? fullMessage.id : null
                } else {
                    console.log('Message not found')

                    let createdDate = moment(fullMessage.publicationDateTime, "YYYYMMDD").valueOf()
                    let receivedDate = new Date().getTime()

                    let newMessage = {
                        created: createdDate,
                        fromAddress: getFromAddress(fullMessage.sender),
                        subject: (fullMessage.document && fullMessage.document.title) || fullMessage.errorCode + " " + fullMessage.title,
                        metas: fullMessage.customMetas,
                        toAddresses: [boxId],
                        fromHealthcarePartyId: "",
                        transportGuid: boxId + ":" + fullMessage.id,
                        received: receivedDate

                    }

                    return iccMessageXApi.newInstance(user, newMessage)
                        .then(messageInstance => msgApi.createMessage(messageInstance))
                        .then(createdMessage => {
                            // console.log(createdMessage)
                            Promise.all((fullMessage.document ? [fullMessage.document] : []).concat(fullMessage.annex || []).map(a => a &&
									docxApi.newInstance(user, createdMessage, {
										documentLocation:   (fullMessage.document && a.content === fullMessage.document.content) ? 'body' : 'annex',
										documentType:       'result', //Todo identify message and set type accordingly
										mainUti:            docxApi.uti(a.mimeType, a.filename && a.filename.replace(/.+\.(.+)/,'$1')),
										name:               a.title
									})
										.then(d => docApi.createDocument(d))
										.then(createdDocument => {
											let byteContent = iccUtils.base64toArrayBuffer(a.content);
											return [createdDocument, byteContent]
										})
										.then(([createdDocument, byteContent]) => docApi.setAttachment(createdDocument.id, null, byteContent)
											.then(att =>
												createdDocument.documentLocation !== "body" && textType(createdDocument.mainUti, createdDocument.otherUtis)?
												beResultApi.getInfos(createdDocument.id)
												.then(docInfos => docInfos?[docInfos,Promise.all(docInfos.map( docInfo => assignAttachment(fullMessage.id,docInfo,createdDocument)))]:[null,null])
												.then(([docInfos,assignedAttachment]) => {
													assignedAttachment && assignedAttachment.then(data => {
														data = data.filter(d=>d)
														createdMessage.unassignedResults = docInfos.filter(docinfo => (data.map(p => p.protocolId) || []).indexOf(docinfo) === -1);
														createdMessage.assignedResults = data.map(p => p.id) || [];
														msgApi.modifyMessage(createdMessage);
													})
												}):att
											)
										)
								)
                            )
                        })
                        .then(() => null) //DO NOT RETURN A MESSAGE ID TO BE DELETED
                }
            })

        boxIds && boxIds.forEach(boxId =>
            ehboxApi.loadMessagesUsingPOST(keystoreId, tokenId, ehpassword, boxId, 100, alternateKeystores)
                .then(messages => {
                    let p = Promise.resolve([])
                    messages.forEach(m => {
                        p = p.then(acc => treatMessage(m, boxId).then(id => id ? acc.concat([id]) : acc).catch(e => {console.log("Error loading message "+m.id); return acc}))
                    })
                    return p
                })
                .then(toBeDeletedIds =>
                    (!boxId.startsWith("BIN")) ? Promise.all(toBeDeletedIds.map(id => ehboxApi.moveMessagesUsingPOST(keystoreId, tokenId, ehpassword, [id], boxId, "BIN" + boxId))) : Promise.resolve([])
                )
                .catch(() => console.log("Error while fetching messages"))
        )
    }
};

function getFromAddress(sender){
    if (!sender) { return "" }
	return (sender.lastName ? sender.lastName : "") +
        (sender.firstName ? ' '+sender.firstName : "") +
        (sender.organizationName ? ' '+sender.organizationName : "") +
        (' ' + sender.identifierType.type + ':' + sender.id)

}
