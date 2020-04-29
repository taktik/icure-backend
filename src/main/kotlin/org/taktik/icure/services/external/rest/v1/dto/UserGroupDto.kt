package org.taktik.icure.services.external.rest.v1.dto

class UserGroupDto {
    var groupId: String? = null
    var userId: String? = null
    var groupName: String? = null

    constructor() {}
    constructor(groupId: String?, userId: String?, groupName: String?) {
        this.groupId = groupId
        this.userId = userId
        this.groupName = groupName
    }

}
