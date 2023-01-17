package com.ccc.proj.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.ccc.proj.common.R;
import com.ccc.proj.entity.User;
import com.ccc.proj.service.UserService;
import com.ccc.proj.util.SMSUtils;
import com.ccc.proj.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送验证码
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成四位数验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);
            //调用阿里云提供的短信服务API发送短信
            //SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, code);
            //需要将生成的验证码和手机号保存到Session中
            //session.setAttribute("phone",phone);
            //session.setAttribute("code", code);
//            设置当前会话的失效时间(验证码5分钟内有效)
//            session.setMaxInactiveInterval(300);

            //将手机号和生成的验证码缓存到redis中,并且设置有效期为5分钟
            stringRedisTemplate.opsForValue().set("phone",phone);
            stringRedisTemplate.opsForValue().set("code",code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中取出验证码和手机号
//        String codeInSession = session.getAttribute("code").toString();
//        String phoneInSession = session.getAttribute("phone").toString();

        //从redis中取出手机号和验证码
        String phoneInSession =  stringRedisTemplate.opsForValue().get("phone").toString();
        String codeInSession = stringRedisTemplate.opsForValue().get("code").toString();

        //进行验证码和手机号的比对（页面提交的和session中保存的比对）
        if (phoneInSession.equals(phone) && codeInSession.equals(code)) {
            //如果能够比对成功，说明登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户登录成功，删除Redis中缓存的验证码和手机号
            stringRedisTemplate.delete("phone");
            stringRedisTemplate.delete("code");

            return R.success(user);
        }
        return R.error("登录失败");
    }

    /**
     * 退出登录
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
