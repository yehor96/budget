package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.Actor;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

@UtilityClass
public class ActorFactory {

    public static final long DEFAULT_ACTOR_ID = 1L;
    public static final long SECOND_ACTOR_ID = 2L;

    public static ActorLimitedDto defaultActorLimitedDto() {
        return ActorLimitedDto.builder()
                .name("John")
                .build();
    }

    public static ActorFullDto defaultActorFullDto() {
        return ActorFullDto.builder()
                .id(DEFAULT_ACTOR_ID)
                .name("John")
                .build();
    }

    public static Actor defaultActor() {
        return Actor.builder()
                .id(DEFAULT_ACTOR_ID)
                .name("John")
                .build();
    }

    public static ActorLimitedDto secondActorLimitedDto() {
        return ActorLimitedDto.builder()
                .name("John")
                .build();
    }

    public static ActorFullDto secondActorFullDto() {
        return ActorFullDto.builder()
                .id(SECOND_ACTOR_ID)
                .name("Sarah")
                .build();
    }

    public static Actor secondActor() {
        return Actor.builder()
                .id(SECOND_ACTOR_ID)
                .name("Sarah")
                .build();
    }
}
