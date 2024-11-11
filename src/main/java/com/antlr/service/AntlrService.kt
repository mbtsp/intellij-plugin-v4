package com.antlr.service

import com.antlr.preview.PreviewState
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

@Service(Service.Level.PROJECT)
class AntlrService(private val project: Project) {
    val logger = Logger.getInstance(AntlrService::class.java)
    private val grammarToPreviewState = mutableMapOf<String, PreviewState>()

    companion object {
        fun getInstance(project: Project): AntlrService {
            return AntlrService(project)
        }
    }

    fun previewState(virtualFile: VirtualFile): PreviewState {
        if (!grammarToPreviewState.containsKey(virtualFile.path)) {
            val previewState = PreviewState(project, virtualFile)
            grammarToPreviewState[virtualFile.path] = previewState
            return previewState
        }
        return grammarToPreviewState[virtualFile.path]!!
    }


}
