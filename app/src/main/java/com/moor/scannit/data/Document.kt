package com.moor.scannit.data

import android.net.Uri
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import java.util.*

@Entity
class Document {
    @Id var id: Long = 0
    var name :String=""
    var createDate: Date?=null
    @Backlink(to="document")
    lateinit var pages: ToMany<Page>
}


@Entity
class Page{
    @Id var id: Long = 0
    var number:Int=0
    var uri: String=""
    var rawText:String=""
    lateinit var document: ToOne<Document>
}

