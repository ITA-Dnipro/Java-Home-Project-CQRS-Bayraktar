package com.softserveinc.ita.homeproject.application.api;

import static com.softserveinc.ita.homeproject.application.constants.Permissions.CREATE_QUESTION_PERMISSION;
import static com.softserveinc.ita.homeproject.application.constants.Permissions.CREATE_VOTE_PERMISSION;
import static com.softserveinc.ita.homeproject.application.constants.Permissions.DELETE_QUESTION_PERMISSION;
import static com.softserveinc.ita.homeproject.application.constants.Permissions.GET_POLL_PERMISSION;
import static com.softserveinc.ita.homeproject.application.constants.Permissions.GET_QUESTION_PERMISSION;
import static com.softserveinc.ita.homeproject.application.constants.Permissions.UPDATE_QUESTION_PERMISSION;

import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.softserveinc.ita.homeproject.api.PollsApi;
import com.softserveinc.ita.homeproject.homeservice.dto.PollDto;
import com.softserveinc.ita.homeproject.homeservice.dto.PollQuestionDto;
import com.softserveinc.ita.homeproject.homeservice.dto.VoteDto;
import com.softserveinc.ita.homeproject.homeservice.service.PollQuestionService;
import com.softserveinc.ita.homeproject.homeservice.service.PollService;
import com.softserveinc.ita.homeproject.homeservice.service.VoteService;
import com.softserveinc.ita.homeproject.model.CreateQuestion;
import com.softserveinc.ita.homeproject.model.CreateVote;
import com.softserveinc.ita.homeproject.model.PollStatus;
import com.softserveinc.ita.homeproject.model.PollType;
import com.softserveinc.ita.homeproject.model.QuestionType;
import com.softserveinc.ita.homeproject.model.ReadMultipleChoiceQuestion;
import com.softserveinc.ita.homeproject.model.ReadPoll;
import com.softserveinc.ita.homeproject.model.ReadVote;
import com.softserveinc.ita.homeproject.model.UpdateQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Provider
@Component
public class PollApiImpl extends CommonApi implements PollsApi {

    @Autowired
    private PollQuestionService pollQuestionService;

    @Autowired
    private PollService pollService;

    @Autowired
    private VoteService voteService;

    @PreAuthorize(GET_POLL_PERMISSION)
    @Override
    public Response getPoll(Long id) {
        var pollDto = pollService.getOne(id);
        var readPoll = mapper.convert(pollDto, ReadPoll.class);
        return Response.status(Response.Status.OK).entity(readPoll).build();
    }

    @PreAuthorize(GET_POLL_PERMISSION)
    @Override
    public Response queryPoll(
        Long cooperationId,
        Integer pageNumber,
        Integer pageSize,
        String sort,
        String filter,
        Long id,
        LocalDateTime creationDate,
        LocalDateTime completionDate,
        PollType type,
        PollStatus status
    ) {
        Page<PollDto> readPoll = pollService.findAll(pageNumber, pageSize, getSpecification());
        return buildQueryResponse(readPoll, ReadPoll.class);
    }

    @PreAuthorize(CREATE_QUESTION_PERMISSION)
    @Override
    public Response createQuestion(Long pollId, @Valid CreateQuestion createQuestion) {
        var createQuestionDto = mapper.convert(createQuestion, PollQuestionDto.class);
        var readQuestionDto = pollQuestionService.createPollQuestion(pollId, createQuestionDto);
        var readQuestion = mapper.convert(readQuestionDto, ReadMultipleChoiceQuestion.class);

        return Response.status(Response.Status.CREATED).entity(readQuestion).build();
    }

    @PreAuthorize(CREATE_VOTE_PERMISSION)
    @Override
    public Response createVote(Long pollId, @Valid CreateVote createVote) {
        var createVoteDto = mapper.convert(createVote, VoteDto.class);
        var readVoteDto = voteService.createVote(pollId, createVoteDto);
        var readVote = mapper.convert(readVoteDto, ReadVote.class);
        return Response.status(Response.Status.CREATED).entity(readVote).build();
    }

    @PreAuthorize(DELETE_QUESTION_PERMISSION)
    @Override
    public Response deleteQuestion(Long pollId, Long id) {
        pollQuestionService.deactivatePollQuestion(pollId, id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PreAuthorize(GET_QUESTION_PERMISSION)
    @Override
    public Response getQuestion(Long pollId, Long id) {
        PollQuestionDto toGet = pollQuestionService.getOne(id, getSpecification());
        var readQuestion = mapper.convert(toGet, ReadMultipleChoiceQuestion.class);

        return Response.status(Response.Status.OK).entity(readQuestion).build();
    }

    @PreAuthorize(GET_QUESTION_PERMISSION)
    @Override
    public Response queryQuestion(Long pollId,
                                  @Min(1) Integer pageNumber,
                                  @Min(1) @Max(10) Integer pageSize,
                                  String sort,
                                  String filter,
                                  Long id,
                                  QuestionType type) {
        Page<PollQuestionDto> readQuestion = pollQuestionService.findAll(pageNumber, pageSize, getSpecification());
        return buildQueryResponse(readQuestion, ReadMultipleChoiceQuestion.class);
    }

    @PreAuthorize(UPDATE_QUESTION_PERMISSION)
    @Override
    public Response updateQuestion(Long pollId, Long id, @Valid UpdateQuestion updateQuestion) {
        var updateQuestionDto = mapper.convert(updateQuestion, PollQuestionDto.class);
        var toUpdate = pollQuestionService.updatePollQuestion(pollId, id, updateQuestionDto);
        var readQuestion = mapper.convert(toUpdate, ReadMultipleChoiceQuestion.class);

        return Response.status(Response.Status.OK).entity(readQuestion).build();
    }
}
