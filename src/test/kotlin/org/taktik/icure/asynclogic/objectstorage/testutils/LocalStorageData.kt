package org.taktik.icure.asynclogic.objectstorage.testutils

import java.io.File

private val localStorageFile = File("build/tests/icureCache")

val testLocalStorageDirectory: String = localStorageFile.absolutePath

fun resetTestLocalStorageDirectory() {
	if (localStorageFile.isDirectory) {
		localStorageFile.deleteRecursively()
	}
	localStorageFile.mkdirs()
}
