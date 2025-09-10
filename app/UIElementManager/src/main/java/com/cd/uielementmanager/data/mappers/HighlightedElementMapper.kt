package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.HighlightedElementEntity
import com.cd.uielementmanager.domain.contents.HighlightedElementContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

/**
 * Mapper to convert HighlightedElementEntity (data layer) to HighlightedElement (domain layer)
 */
internal class HighlightedElementMapper : IBaseMapper<HighlightedElementEntity, HighlightedElementContent> {
    
    override fun mapData(data: HighlightedElementEntity): HighlightedElementContent {
        return HighlightedElementContent(
            elementId = data.elementId,
            borderShape = data.borderShape,
            borderStrokeWidth = data.borderStrokeWidth,
            borderRadius = data.borderRadius,
            borderColor = data.borderColor
        )
    }
}