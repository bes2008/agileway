package com.jn.agileway.oauth2.authz.springboot.starter;

import com.bes.um3rd.umclient.tpauthc.TpAuthc;
import com.jn.agileway.oauth2.authz.userinfo.Userinfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping("/auth/oauth2")
@RestController
public class LoginUmController {
    private final TpAuthc tpAuthc;

    public LoginUmController(TpAuthc tpAuthc) {
        this.tpAuthc = tpAuthc;
    }

    /**
     * 在其它平台上挂这个链接，触发访问接入了UM的Client
     */
    @RequestMapping("/login_to_um")
    public void login(HttpServletRequest request,
                      HttpServletResponse response,
                      @RequestParam(name = "client_id", required = false) String clientId, // UM ClientId
                      @RequestParam(name = "path", required = false)
                      String path, // 指定登录成功后跳转的页面
                      @RequestParam(name = "channel", required = false) String channel) throws IOException {
        Userinfo userInfo = (Userinfo) request.getAttribute(OAuth2AuthzHandler.REQUEST_KEY_OAUTH2_AUTHORIZED_USER);
        String username = userInfo.getUsername();
        String ticket = null;
        String tploginActionUri = tpAuthc.getThirdPartyLoginActionUri(username, ticket, clientId, channel, path, null);
        response.sendRedirect(tploginActionUri);
    }
}
