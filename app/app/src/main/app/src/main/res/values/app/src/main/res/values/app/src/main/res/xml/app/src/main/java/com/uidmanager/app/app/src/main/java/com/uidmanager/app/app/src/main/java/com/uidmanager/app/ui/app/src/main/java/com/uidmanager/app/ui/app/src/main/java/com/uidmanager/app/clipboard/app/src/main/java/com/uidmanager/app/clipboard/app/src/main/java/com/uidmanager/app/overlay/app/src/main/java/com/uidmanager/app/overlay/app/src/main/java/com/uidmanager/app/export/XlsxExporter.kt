package com.uidmanager.app.export

import android.content.Context
import com.uidmanager.app.Entry
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object XlsxExporter {

    fun export(ctx: Context, entries: List<Entry>): File {
        val dir = File(ctx.cacheDir, "exports").apply { mkdirs() }
        val file = File(dir, "uid_manager_${System.currentTimeMillis()}.xlsx")

        XSSFWorkbook().use { wb ->
            val sheet = wb.createSheet("Entries")

            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("#")
            header.createCell(1).setCellValue("UID")
            header.createCell(2).setCellValue("Password")
            header.createCell(3).setCellValue("Cookies")

            entries.forEachIndexed { index, entry ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue((index + 1).toDouble())
                row.createCell(1).setCellValue(entry.uid)
                row.createCell(2).setCellValue(entry.password)
                row.createCell(3).setCellValue(entry.cookies)
            }

            for (i in 0..3) sheet.autoSizeColumn(i)

            FileOutputStream(file).use { wb.write(it) }
        }
        return file
    }
}
