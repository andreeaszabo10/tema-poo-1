package fileio.input;

import lombok.Getter;

@Getter
public final class EpisodeInput {
    private String name;
    private Integer duration;
    private String description;

    public EpisodeInput() {
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
