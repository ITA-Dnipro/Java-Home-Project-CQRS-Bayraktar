package com.softserveinc.ita.homeproject.homeservice.service.poll.house;

import java.util.List;

import com.softserveinc.ita.homeproject.homedata.cooperation.house.House;
import com.softserveinc.ita.homeproject.homedata.cooperation.house.HouseRepository;
import com.softserveinc.ita.homeproject.homedata.poll.Poll;
import com.softserveinc.ita.homeproject.homedata.poll.PollRepository;
import com.softserveinc.ita.homeproject.homedata.poll.enums.PollStatus;
import com.softserveinc.ita.homeproject.homeservice.dto.cooperation.house.HouseDto;
import com.softserveinc.ita.homeproject.homeservice.dto.poll.PollDto;
import com.softserveinc.ita.homeproject.homeservice.exception.BadRequestHomeException;
import com.softserveinc.ita.homeproject.homeservice.exception.NotFoundHomeException;
import com.softserveinc.ita.homeproject.homeservice.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PollHouseServiceImpl implements PollHouseService {
    private static final String WRONG_HOUSE = "Can't add or remove house, invalid poll_id or house_id";

    private final PollRepository pollRepository;

    private final HouseRepository houseRepository;

    private final ServiceMapper mapper;

    @Override
    @Transactional
    public PollDto add(Long houseId, Long pollId) {
        Poll poll = getDraftPollById(pollId);
        House house = getHouseById(houseId);

        if (validateHouse(poll, house)) {
            List<House> houses = poll.getPolledHouses();

            if (houses.contains(house)) {
                throw new BadRequestHomeException(
                        String.format("House with id:%s already exists in poll with id:%s", houseId, pollId));
            }

            houses.add(house);

            poll.setPolledHouses(houses);
            pollRepository.save(poll);
        }

        return mapper.convert(poll, PollDto.class);
    }

    @Override
    @Transactional
    public void remove(Long houseId, Long pollId) {
        Poll poll = getDraftPollById(pollId);
        House house = getHouseById(houseId);

        if (validateHouse(poll, house)) {
            List<House> houses = poll.getPolledHouses();
            houses.remove(house);
            pollRepository.save(poll);
        }
    }

    @Override
    public Page<HouseDto> findAll(Integer pageNumber, Integer pageSize, Specification<House> specification) {
        return houseRepository.findAll(specification, PageRequest.of(pageNumber - 1, pageSize))
            .map(news -> mapper.convert(news, HouseDto.class));
    }

    private Poll getDraftPollById(Long id) {
        return pollRepository.findById(id).filter(Poll::getEnabled).filter(p -> p.getStatus().equals(PollStatus.DRAFT))
            .orElseThrow(() -> new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "Poll", id)));
    }

    private House getHouseById(Long id) {
        return houseRepository.findById(id).filter(House::getEnabled)
            .orElseThrow(() -> new NotFoundHomeException(String.format(NOT_FOUND_MESSAGE, "House", id)));
    }

    private boolean validateHouse(Poll poll, House house) {
        Long pollCooperationId = poll.getCooperation().getId();
        Long houseCooperationId = house.getCooperation().getId();
        if (pollCooperationId.equals(houseCooperationId)) {
            return true;
        } else {
            throw new NotFoundHomeException(WRONG_HOUSE);
        }
    }

}
