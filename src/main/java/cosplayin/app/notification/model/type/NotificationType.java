package cosplayin.app.notification.model.type;

import lombok.Getter;

@Getter
public enum NotificationType {
    POST_LIKE(true,  TargetKind.POST),
    COMMENT_LIKE(true,  TargetKind.COMMENT),
    POST_COMMENT(false, TargetKind.COMMENT),
    COMMENT_REPLY(false, TargetKind.COMMENT),
    FOLLOW(false, TargetKind.NONE);

    public enum TargetKind { NONE, POST, COMMENT }

    private final boolean aggregated;
    private final TargetKind targetKind;

    NotificationType(boolean aggregated, TargetKind targetKind) {
        this.aggregated = aggregated;
        this.targetKind = targetKind;
    }

}
