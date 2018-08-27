
import * as fhcApi from '../elements/fhc-api/fhcApi'
import * as iccApi from 'icc-api/icc-api/iccApi'

onmessage = function(e){
    if(e.data.action == "loadEhboxMessage"){

        const iccHost       = e.data.iccHost
        const iccHeaders    = JSON.parse(e.data.iccHeaders)

        const fhcHost       = e.data.fhcHost
        const fhcHeaders    = JSON.parse(e.data.fhcHeaders)

        const tokenId       = e.data.tokenId
        const keystoreId    = e.data.keystoreId
        const user          = e.data.user
        const ehpassword    = e.data.ehpassword
        const boxId         = e.data.boxId

        const ehboxApi      = new fhcApi.fhcEhboxcontrollerApi(fhcHost, fhcHeaders)

        //const docApi        = new iccApi.iccDocumentApi(iccHost, iccHeaders)
        const msgApi        = new iccApi.iccMessageApi(iccHost, iccHeaders)
        //const beResultApi   = new iccApi.iccBeresultimportApi(iccHost, iccHeaders)

        ehboxApi.loadMessagesUsingGET(keystoreId, tokenId, ehpassword, boxId, 100).then(messages => {
            messages.map(message => {
                  ehboxApi.getFullMessageUsingGET(keystoreId, tokenId, ehpassword, boxId,message.id).then(contentMessage => {
                      console.log(contentMessage)

                      //msgApi.findMessagesByTransportGuid(boxId+":"+message.id, null, null, 1000).then(m => {
                      //    console.log(m)
                      //})

                  })
                })

            if(messages.length > 0){
                var nbOfEhboxInboxMessage = messages.length
                postMessage({message: "Vous avez reçu "+nbOfEhboxInboxMessage+" nouveau(x) message(s) dans votre ehbox"})
            }
        })
    }
};