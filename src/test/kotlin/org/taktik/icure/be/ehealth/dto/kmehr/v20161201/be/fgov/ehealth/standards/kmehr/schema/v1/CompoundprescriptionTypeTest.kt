package org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.schema.v1

import org.junit.Test

import org.junit.Assert.*
import org.taktik.icure.be.ehealth.dto.kmehr.v20161201.be.fgov.ehealth.standards.kmehr.dt.v1.TextType
import java.io.ByteArrayOutputStream
import javax.xml.bind.JAXBContext



class CompoundprescriptionTypeTest {

    @Test
    fun testCompoundPrescriptionWithTextType() {
        val jaxbContext = JAXBContext.newInstance(Kmehrmessage::class.java)
        val res0 = ByteArrayOutputStream().also { os ->
            jaxbContext.createMarshaller().marshal(Kmehrmessage().apply {
                this.getFolders().add(FolderType().apply {
                    this.getTransactions().add(TransactionType().apply {
                        this.getHeadingsAndItemsAndTexts().add(TextType().apply { value = "Hello" })
                        this.getHeadingsAndItemsAndTexts().add(ItemType().apply {
                            this.getContents().add(ContentType().apply {
                                compoundprescription = CompoundprescriptionType()
                            })
                        })
                    })
                })
            }, os)
        }.toString("UTF8")
        assertNotNull(res0)

        val res2 = ByteArrayOutputStream().also { os ->
            jaxbContext.createMarshaller().marshal(Kmehrmessage().apply {
                this.getFolders().add(FolderType().apply {
                    this.getTransactions().add(TransactionType().apply {
                        this.getHeadingsAndItemsAndTexts().add(ItemType().apply {
                            this.getContents().add(ContentType().apply {
                                compoundprescription = CompoundprescriptionType().apply {
                                    this.getContent().add(ObjectFactory().createCompoundprescriptionTypeMagistraltext(TextType().apply { value = "Hello" }))
                                }
                            })
                        })
                    })
                })
            }, os)
        }.toString("UTF8")
        assertNotNull(res2)
    }

}
