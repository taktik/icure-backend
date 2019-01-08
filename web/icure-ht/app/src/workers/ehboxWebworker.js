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
			//return (uti && [uti] || []).concat(utis && utis.value || []).map(u => iccDocumentXApi.mimeType(u)).find(m => m === 'text/plain');
            // NOTE: mime type and extension from ehbox are not reliable, the ResultImport API can detect if it's the correct type
			return true
		}

        const removeMsg = (msg) => {
            if (msg) {
                const thisBox = msg.transportGuid.substring(0,msg.transportGuid.indexOf(':'))
                const delBox = thisBox == 'INBOX' ? 'BININBOX' : null
                const idOfMsg = msg.transportGuid.substring(msg.transportGuid.indexOf(':')+1)
                // console.log('remove',idOfMsg,thisBox,delBox)
                if (msg.transportGuid && !msg.transportGuid.startsWith("BIN")) {
                    // console.log('move to bin',idOfMsg,thisBox,delBox)
                    return ehboxApi.moveMessagesUsingPOST(keystoreId, tokenId, ehpassword, [idOfMsg], thisBox, delBox)
                        .then(()=>{
                            // console.log('move to bin done, then modify',idOfMsg,thisBox,delBox)
                            msg.transportGuid = delBox + msg.transportGuid.substring(msg.transportGuid.indexOf(':'))
                            msgApi.modifyMessage(msg).then(()=> {
                                // console.log('modify done',idOfMsg,thisBox,delBox)

                            } )
                        })
                        .catch(err => {
                            // console.log('ERROR: move to bin',idOfMsg,thisBox,delBox, err)
                        })
                } else {
                    // console.log('delete',idOfMsg,thisBox,delBox)
                    return ehboxApi.deleteMessagesUsingPOST(keystoreId, tokenId, ehpassword, [idOfMsg], delBox)
                }
            }
        }

        const removeMsgFromEhboxServer = (msg) => {
            if (msg) {
                const thisBox = msg.transportGuid.substring(0,msg.transportGuid.indexOf(':'))
                const delBox = thisBox == 'INBOX' ? 'BININBOX' : thisBox == 'SENTBOX' ? 'BINSENTBOX' : null
                const idOfMsg = msg.transportGuid.substring(msg.transportGuid.indexOf(':')+1)
                // console.log('remove from server',idOfMsg,thisBox,delBox)
                if (thisBox.transportGuid && !thisBox.transportGuid.startsWith("BIN")) { // if it was not in bin
                    // console.log('move to bin',idOfMsg,thisBox,delBox)
                    return ehboxApi.moveMessagesUsingPOST(keystoreId, tokenId, ehpassword, [idOfMsg], thisBox, delBox)
                        .then(()=>{
                            // console.log('move to bin done',idOfMsg,thisBox,delBox)
                        })
                        .catch(err => {
                            // console.log('ERROR: move to bin',idOfMsg,thisBox,delBox, err)
                        })
                } else { // if already in bin, del forever
                    // console.log('delete',idOfMsg,thisBox)
                    return ehboxApi.deleteMessagesUsingPOST(keystoreId, tokenId, ehpassword, [idOfMsg], thisBox)
                }
            }
        }

		const assignResult = (message,docInfo,document) => {
            console.log('assignResult',message,docInfo,document)
            // assign to patient/contact the result matching docInfo from all the results of the document
            // return {id: contactId, protocolId: protocolIdString} if success else null (in promise)
            if (textType(document.mainUti, document.otherUtis)) {
                return iccPatientApi.findByNameBirthSsinAuto(user.healthcarePartyId, docInfo.lastName + " " + docInfo.firstName, null, null, 100, "asc").then(patients => {
                    if (patients && patients.rows[0]) {
                        let thisPat = patients.rows[0]
                        if (patients.rows.length > 0) {
                            // console.log('multiple match')
                            patients.rows.map(pat=>{
                                if (pat.lastName.toUpperCase() === docInfo.lastName.toUpperCase() &&
                                    pat.firstName.toUpperCase() === docInfo.firstName.toUpperCase() &&
                                    pat.dateOfBirth === docInfo.dateOfBirth) {
                                    // console.log('occurence found',pat)
                                    thisPat = pat
                                }
                            })
                        }
                        // console.log('pat > ',thisPat)
                        return iccContactXApi.newInstance(user, thisPat, {
                            groupId: message.id,
                            created: new Date().getTime(),
                            modified: new Date().getTime(),
                            author: user.id,
                            responsible: user.healthcarePartyId,
                            openingDate: moment().format('YYYYMMDDhhmmss') || '',
                            closingDate: moment().format('YYYYMMDDhhmmss') || '',
                            encounterType: {
                                type: docInfo.codes.type,
                                version: docInfo.codes.version,
                                code: docInfo.codes.code
                            },
                            // cryptedForeignKeys: thisPat.cryptedForeignKeys,
                            descr: docInfo.labo,
                            subContacts: []
                        }).then(c => {
                            c.services.push({
                                id: iccCryptoXApi.randomUuid(),
                                codes: [{type:'labResult',code:c.id}],
                                label: 'labResult',
                                content:{
                                    'patientName': {stringValue: thisPat.lastName.toUpperCase()+' '+thisPat.firstName+' ('+thisPat.ssin+')'},
                                    'patientId': {stringValue: thisPat.id}
                                }
                            })
                            console.log('c services',c.services)
                            return iccContactApi.createContact(c)
                        }).then(c => {
                            console.log('createContact',c)
                            return iccFormXApi.newInstance(user, thisPat, {
                                contactId: c.id,
                                descr: "Lab " + new Date().getTime(),
                            }).then(f => {
                                // console.log('should do Import',f)
                                return iccFormXApi.createForm(f).then(f =>
                                    iccHcpartyApi.getHealthcareParty(user.healthcarePartyId).then(hcp =>
                                        beResultApi.doImport(document.id, user.healthcarePartyId, hcp.languages.find(e => !!e) || "en", docInfo.protocol, f.id, null, c)
                                    )
                                )
                            })
                        }).then(c => {
                            console.log("did import ", c, docInfo);
                            return {id: c.id, protocolId: docInfo.protocol}
                        }).catch(err => {
                            console.log(err)
                        })
                    } else {
                        console.log("pat not found:", docInfo.lastName + " " + docInfo.firstName)
                        return Promise.resolve()
                    }
                })
            } else {
                // console.log("message not text type")
                return Promise.resolve()
            }
        } // assignResult end

        const treatMessage =  (message,boxId) => {
            // console.log('treatMessage',message,boxId)
            // if (localStorage.getItem('receiveMailAuto') && localStorage.getItem('receiveMailAuto') === 'false') {
            //     console.log('Automatic ehbox treatMessage disabled by user param')
            // } else {
                return ehboxApi.getFullMessageUsingGET(keystoreId, tokenId, ehpassword, boxId, message.id)
                    .then(fullMessage => msgApi.findMessagesByTransportGuid(boxId+":"+message.id, null, null, 1).then(existingMess => [fullMessage, existingMess]))
                    .then(([fullMessage, existingMess]) => {
                        if (existingMess.rows.length > 0) {
                            //console.log("Message already known in DB",existingMess.rows)
                            const existingMessage = existingMess.rows[0]
                            // remove messages older than 24h
                            if(existingMessage.created !== null && existingMessage.created < (Date.now() - (24 * 3600000))) {
                                return removeMsgFromEhboxServer(existingMessage)
                            }
                            return Promise.resolve()
                        } else {
                            console.log('fullMessage',fullMessage)
                            console.log('boxId',boxId)
                            registerNewMessage(fullMessage, boxId)
                                .then(([createdMessage, annexDocs]) => {
                                    return treatAnnexes(createdMessage, fullMessage, annexDocs, boxId)
                                })
                        }
                    })
            // }
        }

        const treatAnnexes = (createdMessage, fullMessage, annexDocs, boxId) => {
            // console.log('treatAnnex',createdMessage, fullMessage, annexDocs, boxId)
            if (boxId == "INBOX" && annexDocs) { // only import annexes in inbox
                let results = annexDocs.filter(doc => doc.documentLocation !== "body").map(doc => {
                    return treatAnnex(fullMessage, doc)
                }).flat()

                return Promise.all(results)
                    .then (reslist => {
                        let assignedMap = {}
                        let unassignedList = []
                        reslist.flat().forEach(result => {
                            if (result.assigned) {
                                assignedMap[result.contactId] = result.protocolId
                            } else {
                                unassignedList.push(result.protocolId)
                            }
                        })
                        createdMessage.unassignedResults = unassignedList
                        createdMessage.assignedResults = assignedMap
                        // console.log('treatAnnex, createdMessage', createdMessage)
                        return msgApi.modifyMessage(createdMessage).then(msg => {
                            // console.log('msg',msg)
                            if(createdMessage.unassignedResults.length == 0 && createdMessage.assignedResults.length >= 1 ) {
                                return removeMsg(msg)
                            }
                            return Promise.resolve()
                        });
                    })
            } else {
                return Promise.resolve()
            }
        }

        const treatAnnex = (fullMessage, createdDocument) => {
            // console.log('treatAnnex',fullMessage,createdDocument)
            return beResultApi.getInfos(createdDocument.id)
                .then(docInfos => {
                    console.log('treatAnnex',fullMessage,createdDocument,docInfos)
                    return Promise.all(
                        docInfos.map(docInfo => {
                           return assignResult(fullMessage, docInfo, createdDocument).then(result => {
                               if(result != null) {
                                   console.log('result',result)
                                   return {assigned: true, protocolId: result.protocolId, contactId: result.id}
                               } else {
                                   return {assigned: false, protocolId: docInfo.protocol, contactId: null}
                               }
                           })
                        } )
                    )
                })
                .catch(err => {
                    // console.log("document can not be parsed", createdDocument)
                    return []
                })
        }

        const registerNewMessage = (fullMessage, boxId) => {
            // console.log('registerNewMessage',fullMessage,boxId)
            let createdDate = moment(fullMessage.publicationDateTime, "YYYYMMDD").valueOf()
            let receivedDate = new Date().getTime()

            let tempStatus = fullMessage.status ? fullMessage.status : 0<<0 | 1<<1
            if (!fullMessage.status ) {
                tempStatus = fullMessage && fullMessage.important ? tempStatus|1<<2 : tempStatus
                tempStatus = fullMessage && fullMessage.encrypted ? tempStatus|1<<3 : tempStatus
                tempStatus = fullMessage && fullMessage.annex.length ? tempStatus|1<<4 : tempStatus
            }

            (fullMessage.destinations).forEach(dest=>{
                //
            })

            let newMessage = {
                created: createdDate,
                fromAddress: getFromAddress(fullMessage.sender),
                subject: (fullMessage.document && fullMessage.document.title) || fullMessage.errorCode + " " + fullMessage.title,
                metas: fullMessage.customMetas,
                toAddresses: [boxId],
                transportGuid: boxId + ":" + fullMessage.id,
                fromHealthcarePartyId: fullMessage.fromHealthcarePartyId ? fullMessage.fromHealthcarePartyId : fullMessage.sender.id,
                received: receivedDate,
                status: tempStatus
            }
            // console.log('new message : ', newMessage)
            // console.log('its status', isUnread(fullMessage), isImportant(fullMessage), isCrypted(fullMessage))

            return iccMessageXApi.newInstance(user, newMessage)
                .then(messageInstance => msgApi.createMessage(messageInstance))
                .then(createdMessage => {
                    // register body and annexes as documents
                    let annexPromises = (fullMessage.document ? [fullMessage.document] : []).concat(fullMessage.annex || []).map(a => {
                        if (a == null) {
                            console.log("annex is null")
                            return null
                        } else {
                            return registerNewDocument(a, createdMessage, fullMessage)
                        }
                    }).filter(a => a != null)
                    return Promise.all(annexPromises)
                        .then(annexDocs => {
                            return [createdMessage, annexDocs]
                        })
                })
        }

        const registerNewDocument = (document, createdMessage, fullMessage) => {
            let a = document
            // console.log('registerNewDocument',a)
            return docxApi.newInstance(user, createdMessage, {
                documentLocation:   (fullMessage.document && a.content === fullMessage.document.content) ? 'body' : 'annex',
                documentType:       'result', //Todo identify message and set type accordingly
                mainUti:            docxApi.uti(a.mimeType, a.filename && a.filename.replace(/.+\.(.+)/,'$1')),
                name:               a.filename
            })
                .then(d => docApi.createDocument(d))
                .then(createdDocument => {
                    //console.log('createdDocument',createdDocument)
                    let byteContent = iccUtils.base64toArrayBuffer(a.content);
                    return [createdDocument, byteContent]
                })
                .then(([createdDocument, byteContent]) => {
                    return docApi.setAttachment(createdDocument.id, null, byteContent).then(() =>{
                        return createdDocument
                    })
                })
        }


        boxIds && boxIds.forEach(boxId =>{
            // console.log('boxids foreach',keystoreId, tokenId, ehpassword, boxId, 100, alternateKeystores)
            ehboxApi.loadMessagesUsingPOST(keystoreId, tokenId, ehpassword, boxId, 100, alternateKeystores)
                .then(messages => {
                    let p = Promise.resolve([])
                    messages.forEach(m => {
                        // console.log(m,'its status', isUnread(m), isImportant(m), isCrypted(m))
                        p = p.then(() => {
                            return treatMessage(m, boxId)
                                .catch(e => {console.log("Error processing message "+m.id,e); return Promise.resolve()})
                        })
                    })
                    return p
                })
                .catch(err => console.log("Error while fetching messages: " + err))
        })
    }
};

function isUnread(m) {
    return (m.status & (1 << 1))
}
function isImportant(m) {
    return (m.status & (1 << 2))
}
function isCrypted(m) {
    return (m.status & (1 << 3))
}
function hasAnnex(m) {
    return (m.status & (1 << 4))
}

function getFromAddress(sender){
    if (!sender) { return "" }
    return (sender.lastName ? sender.lastName : "") +
        (sender.firstName ? ' '+sender.firstName : "") +
        (sender.organizationName ? ' '+sender.organizationName : "") +
        (' ' + sender.identifierType.type + ':' + sender.id)

}
