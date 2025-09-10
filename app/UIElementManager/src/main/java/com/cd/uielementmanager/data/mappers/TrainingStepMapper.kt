package com.cd.uielementmanager.data.mappers

import com.cd.uielementmanager.data.entities.TrainingStepEntity
import com.cd.uielementmanager.domain.contents.TrainingStepContent
import com.cd.uielementmanager.domain.domain_utils.IBaseMapper

/**
 * Mapper to convert TrainingStepEntity (data layer) to TrainingStep (domain layer)
 */
internal class TrainingStepMapper : IBaseMapper<TrainingStepEntity, TrainingStepContent> {
    
    private val highlightedElementMapper = HighlightedElementMapper()
    
    override fun mapData(data: TrainingStepEntity): TrainingStepContent {
        return TrainingStepContent(
            id = data.id,
            stepNumber = data.stepNumber,
            screenName = data.screenName,
            highlightedElementContent = highlightedElementMapper.mapData(data.highlightedElement),
            instructions = data.instructions
        )
    }
}