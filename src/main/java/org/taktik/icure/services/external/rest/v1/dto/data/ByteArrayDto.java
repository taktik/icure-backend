package org.taktik.icure.services.external.rest.v1.dto.data;


import java.io.Serializable;

public class ByteArrayDto implements Serializable{
    byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
