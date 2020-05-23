package com.moor.scannit.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class Folder (
    @Id
    var id: Long = 0,
    var name: String? = null,
    var create_date: Date
)