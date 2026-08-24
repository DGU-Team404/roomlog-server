package com.roomlog.chat.dto;

import com.roomlog.chat.domain.AppGuide;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuggestedQuestion {

    private final String guide;

    private final String question;

    public static SuggestedQuestion from(AppGuide guide) {
        return new SuggestedQuestion(guide.name(), guide.getSuggestedQuestion());
    }
}
