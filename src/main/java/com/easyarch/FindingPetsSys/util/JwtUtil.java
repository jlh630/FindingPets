

package com.easyarch.FindingPetsSys.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.easyarch.FindingPetsSys.exception.AuthenticationException;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    //密钥
    private static final String KEY = "FiningPet";
    private static final String ISSUER = "jlh";
    private static final String AUDIENCE = "Client";
    private static final Integer EXPIRE_TIME_HOUR = 2;
    //头部
    private static final Map<String, Object> HEADER_MAP = new HashMap<String, Object>() {
        {
            this.put("alg", "HS256");
            this.put("typ", "JWT");
        }
    };
    //签名
    private static final Algorithm ALGORITHM_KEY = Algorithm.HMAC256(KEY);

    /**
     * 计出过期的时间
     *
     * @param nowDate 当前时间
     * @return 当前时间+推移量
     */
    private static Date expireDate(Date nowDate) {
        if (nowDate == null) {
            nowDate = new Date();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.HOUR_OF_DAY, EXPIRE_TIME_HOUR);
        return calendar.getTime();
    }

    /**
     * 生成token
     *
     * @param jti     tokenId
     * @param payload 载荷
     * @return token
     */
    public static String createToken(String jti, Map<String, String> payload) {
        Date expire = expireDate(new Date());
        JWTCreator.Builder jwt = JWT.create();

        for (Map.Entry<String, String> entry : payload.entrySet()) {
            jwt.withClaim(entry.getKey(), entry.getValue());
        }

        return jwt.withHeader(HEADER_MAP)
                .withJWTId(jti)
                .withIssuer(ISSUER)
                .withAudience(AUDIENCE)
                .withExpiresAt(expire)
                .sign(ALGORITHM_KEY);
    }

    /**
     * 验证token
     *
     * @param token token
     * @return 载荷
     * @throws AuthenticationException 签名、签发者..等问题
     */
    public static Map<String, Claim> verifierToken(String token) throws AuthenticationException {
        if (token != null && !token.isEmpty()) {
            JWTVerifier verifier = JWT.require(ALGORITHM_KEY).build();
            DecodedJWT jwt;

            try {
                jwt = verifier.verify(token);
            } catch (Exception e) {
                throw new AuthenticationException("token is error");
            }

            String issuer = jwt.getIssuer();
            if (!issuer.equals(ISSUER)) {
                throw new AuthenticationException("issuer is error");
            } else {
                String audience = jwt.getAudience().get(0);
                if (!audience.equals(AUDIENCE)) {
                    throw new AuthenticationException("audience is error");
                } else {
                    return jwt.getClaims();
                }
            }
        } else {
            throw new AuthenticationException("token is null");
        }
    }

    /**
     * 生成tokenId
     *
     * @return tokenId
     */
    public static String createJTI() {
        return System.currentTimeMillis() + "";
    }
}
