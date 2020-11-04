package wee.digital.ft.ui.face

import wee.digital.ft.repository.dto.FaceResponse

class FaceArg {

    val userIdList: List<String>

    constructor(response: FaceResponse) {
        userIdList = response.userID
    }

    private constructor(){
        userIdList = listOf("s")
    }

    companion object{
        val testArg = FaceArg()
    }

}