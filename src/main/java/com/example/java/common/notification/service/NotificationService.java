package com.example.java.common.notification.service;

import org.springframework.stereotype.Service;

import com.example.java.common.notification.entity.Notification;
import com.example.java.common.notification.entity.NotificationRecipientType;
import com.example.java.common.notification.entity.NotificationReferenceType;
import com.example.java.common.notification.entity.NotificationType;
import com.example.java.common.notification.repository.NotificationRepository;
import com.example.java.groupbuy.entity.Participation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification notifyGroupBuyPromoted(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.GROUP_BUY_PROMOTED)
                .title("공구 대기열 승격 안내")
                .content("대기열에서 승격됐어요. 기한 내 결제해 주세요.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }

    public Notification notifyGroupBuyConfirmed(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.GROUP_BUY_CONFIRMED)
                .title("공동구매 확정 안내")
                .content("참여하신 공동구매가 확정되었어요. 마감 후 순차 배송됩니다.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }

    public Notification notifyGroupBuyFailed(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.GROUP_BUY_FAILED)
                .title("공동구매 무산 안내")
                .content("참여하신 공동구매가 최소 인원 미달로 무산되었어요.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }

    public Notification notifyPaymentDone(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.PAYMENT_DONE)
                .title("공동구매 결제 완료")
                .content("공동구매 결제가 완료되어 참여 중 상태로 변경되었습니다.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }

    public Notification notifyRefundDone(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.REFUND_DONE)
                .title("환불 완료 안내")
                .content("공동구매 결제 취소(환불)가 완료되었습니다.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }

    public Notification notifyRefundFailed(Participation participation) {
        return notificationRepository.save(Notification.builder()
                .type(NotificationType.REFUND_FAILED)
                .title("환불 실패 안내")
                .content("공동구매 환불 처리 중 오류가 발생했습니다. 고객센터로 문의해 주세요.")
                .recipientType(NotificationRecipientType.MEMBER)
                .recipientSeq(participation.getMemberSeq())
                .referenceType(NotificationReferenceType.PARTICIPATION)
                .referenceSeq(participation.getSeq())
                .build());
    }
}

