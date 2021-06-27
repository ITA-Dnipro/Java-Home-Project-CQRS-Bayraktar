package com.softserveinc.ita.homeproject.application.mapper.config.impl;

import com.softserveinc.ita.homeproject.application.mapper.HomeMapper;
import com.softserveinc.ita.homeproject.application.mapper.config.HomeMappingConfig;
import com.softserveinc.ita.homeproject.homeservice.dto.AdviceQuestionVoteDto;
import com.softserveinc.ita.homeproject.homeservice.dto.QuestionVoteDto;
import com.softserveinc.ita.homeproject.model.CreateAdviceQuestionVote;
import lombok.RequiredArgsConstructor;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdviceQuestionVoteDtoHomeMappingConfig implements
    HomeMappingConfig<CreateAdviceQuestionVote, QuestionVoteDto> {

    @Lazy
    private final HomeMapper homeMapper;

    @Override
    public void addMappings(TypeMap<CreateAdviceQuestionVote, QuestionVoteDto> typeMap) {
        typeMap.setProvider(request -> homeMapper.convert(request.getSource(), AdviceQuestionVoteDto.class));
    }
}
