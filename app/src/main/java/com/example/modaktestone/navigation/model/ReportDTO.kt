package com.example.modaktestone.navigation.model

data class ReportDTO(
    var uidWhoReported: String? = null,
    var targetContent: String? = null,
    var targetComment: String? = null,
    // 0: "- 게시판 성격에 부적절해요",
    // 1: "- 욕설과 비하가 담겨있어요",
    // 2: "- 상업광고 및 판매글이에요",
    // 3: "- 음란물 및 불건전한 내용이 있어요",
    // 4: "- 도배가 되어 있는 글이에요"
    var kind: Int? = null,
    var title: String? = null,
    var explain: String? = null,
    var timestamp: Long? = null,
    var time: String? = null
)
