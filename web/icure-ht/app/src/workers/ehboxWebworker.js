
//importScripts()

onmessage = function(e){
    if(e.data.action == "loadEhboxMessage"){

        const tokenId = e.data.tokenId
        const keystoreId = e.data.keystoreId
        const user = e.data.user

        postMessage({message: "Il y a xxx message dans l'ehbox"})

        /*
        this.api.fhc().Ehboxcontroller().loadMessagesUsingGET(this.api.keystoreId, this.api.tokenId, this.credentials.ehpassword, "INBOX", 100).then(messages => {
            console.log(messages)
            if(messages.length > 0){
                var nbOfEhboxInboxMessage = messages.length
                postMessage("Il y a "+nbOfEhboxInboxMessage+"message dans l'ehbox")
            }
        })
        */
    }
};


