package wee.digital.fpa.ui.face

import wee.digital.fpa.repository.dto.FaceResponse

class FaceArg {

    val userIdList: List<String>

    constructor(response: FaceResponse) {
        userIdList = response.userID
    }

}