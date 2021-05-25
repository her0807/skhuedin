package com.skhuedin.skhuedin.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CheckTokenWithCommonResponse<T> extends CommonResponse {

    String token;
    boolean check;

    public CheckTokenWithCommonResponse(T data, String token, boolean check) {
        super(data);
        this.token = token;
        this.check = check;
    }
}
