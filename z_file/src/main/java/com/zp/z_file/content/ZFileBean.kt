package com.zp.z_file.content

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ZFileBean(
        var fileName: String = "",      // 文件名
        var isFile: Boolean = true,     // true---文件；false---文件夹
        var filePath: String = "",      // 文件路径
        var date: String = "",          // 格式化后的时间
        var originalDate: String = "",  // 原始时间
        var size: String = "",          // 格式化后的大小
        var originaSize: Long = 0L,     // 原始大小
        var parent: String? = ""
) : Serializable, Parcelable {

        constructor(parcel: Parcel) : this(
                fileName = parcel.readString() ?: "",
                isFile = parcel.readInt() == 1,
                filePath = parcel.readString() ?: "",
                date = parcel.readString() ?: "",
                originalDate = parcel.readString() ?: "",
                size = parcel.readString() ?: "",
                originaSize = parcel.readLong(),
                parent = parcel.readString()
        )

        override fun describeContents(): Int = 0

        override fun writeToParcel(dest: Parcel?, flags: Int) {
                dest?.writeString(fileName)
                dest?.writeInt(if (isFile) 1 else 0)
                dest?.writeString(filePath)
                dest?.writeString(date)
                dest?.writeString(originalDate)
                dest?.writeString(size)
                dest?.writeLong(originaSize)
                dest?.writeString(parent)

        }

        companion object CREATOR : Parcelable.Creator<ZFileBean> {

                override fun createFromParcel(parcel: Parcel): ZFileBean {
                        return ZFileBean(parcel)
                }

                override fun newArray(size: Int): Array<ZFileBean?> {
                        return arrayOfNulls(size)
                }
        }
}

internal data class ZFileInfoBean(
        var duration: String = "",
        var width: String = "",
        var height: String = ""
)

internal data class ZFilePathBean(
        var fileName: String = "", // 路径名称
        var filePath: String = "" // 文件路径
)

internal data class ZFileQWBean(
        var zFileBean: ZFileBean? = null,
        var isSelected: Boolean = true
)



 