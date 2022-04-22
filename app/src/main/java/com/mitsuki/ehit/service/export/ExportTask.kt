package com.mitsuki.ehit.service.export

sealed class ExportTask() {

    class ZipExport: ExportTask() {

    }

    class ImageExport: ExportTask() {

    }
}