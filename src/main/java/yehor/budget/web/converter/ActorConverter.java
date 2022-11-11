package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Actor;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

@Component
public class ActorConverter {

    public ActorFullDto convert(Actor actor) {
        return ActorFullDto.builder()
                .id(actor.getId())
                .name(actor.getName())
                .build();
    }

    public Actor convert(ActorLimitedDto actorDto) {
        return Actor.builder()
                .name(actorDto.getName())
                .build();
    }

    public Actor convert(ActorFullDto actorDto) {
        return Actor.builder()
                .id(actorDto.getId())
                .name(actorDto.getName())
                .build();
    }
}
