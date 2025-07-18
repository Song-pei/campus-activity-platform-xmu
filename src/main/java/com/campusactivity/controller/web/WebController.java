package com.campusactivity.controller.web;

import com.campusactivity.entity.Activity;
import com.campusactivity.entity.ActivityType;
import com.campusactivity.service.ActivityService;
import com.campusactivity.service.ActivityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.campusactivity.entity.Registration;
import com.campusactivity.service.RegistrationService;

@Controller
public class WebController {
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private ActivityTypeService activityTypeService;
    
    /**
     * 首页 - 显示欢迎页面和快速导航
     * 可以在这里添加数据，传递给前端模板
     */
    @GetMapping("/")
    public String index(Model model) {
        // 可以添加一些数据传递给前端
        model.addAttribute("welcomeMessage", "欢迎来到厦门大学校园活动平台");
        model.addAttribute("totalActivities", activityService.getAllActivities().size());

        return "index"; // 返回 templates/index.html 模板
    }
    
    /**
     * 用户活动页面
     */
    @GetMapping("/user/activities/page")
    public String userActivities(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        model.addAttribute("title", "用户活动列表");
        return "user/activity-list";
    }

    /**
     * 用户活动详情页面
     */
    @GetMapping("/user/activities/page/{id}")
    public String userActivityDetail(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        model.addAttribute("activity", activity);
        model.addAttribute("title", "活动详情");
        return "user/activity-detail";
    }
    
    /**
     * 管理员活动页面
     */
    @GetMapping("/admin/activities/page")
    public String adminActivities(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        model.addAttribute("title", "管理员活动列表");
        return "admin/activity-list";
    }

    /**
     * 管理员活动详情页面
     */
    @GetMapping("/admin/activities/page/{id}")
    public String adminActivityDetail(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return "redirect:/admin/activities/page";
        }
        model.addAttribute("activity", activity);
        model.addAttribute("title", "活动详情 - " + activity.getTitle());
        return "admin/activity-detail";
    }

    /**
     * 显示新增活动表单页面
     */
    @GetMapping("/admin/activities/new")
    public String showCreateActivityForm(Model model) {
        List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
        model.addAttribute("activityTypes", activityTypes);
        model.addAttribute("activity", new Activity());
        model.addAttribute("title", "新增活动");
        return "admin/activity-form";
    }

    /**
     * 处理新增活动表单提交
     */
    @PostMapping("/admin/activities/new")
    public String createActivity(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String startTime,
                                @RequestParam String endTime,
                                @RequestParam String location,
                                @RequestParam(required = false) Integer capacity,
                                @RequestParam(required = false) Long activityTypeId,
                                RedirectAttributes redirectAttributes) {
        try {
            // 创建新活动对象
            Activity activity = new Activity();
            activity.setTitle(title);
            activity.setDescription(description);
            activity.setLocation(location);
            activity.setCapacity(capacity);

            // 解析时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            if (startTime != null && !startTime.isEmpty()) {
                activity.setStartTime(dateFormat.parse(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                activity.setEndTime(dateFormat.parse(endTime));
            }

            // 设置活动类型
            if (activityTypeId != null) {
                ActivityType activityType = activityTypeService.getActivityTypeById(activityTypeId);
                activity.setActivityType(activityType);
            }

            // 保存活动
            activityService.createActivity(activity);

            redirectAttributes.addFlashAttribute("successMessage", "活动创建成功！");
            return "redirect:/admin/activities/page";

        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "时间格式错误，请重新输入！");
            return "redirect:/admin/activities/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "创建活动失败：" + e.getMessage());
            return "redirect:/admin/activities/new";
        }
    }

    /**
     * 显示编辑活动表单页面
     */
    @GetMapping("/admin/activities/edit/{id}")
    public String showEditActivityForm(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return "redirect:/admin/activities/page";
        }

        List<ActivityType> activityTypes = activityTypeService.getAllActivityTypes();
        model.addAttribute("activityTypes", activityTypes);
        model.addAttribute("activity", activity);
        model.addAttribute("title", "编辑活动 - " + activity.getTitle());
        model.addAttribute("isEdit", true);
        return "admin/activity-form";
    }

    /**
     * 处理编辑活动表单提交
     */
    @PostMapping("/admin/activities/edit/{id}")
    public String updateActivity(@PathVariable Long id,
                                @RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String startTime,
                                @RequestParam String endTime,
                                @RequestParam String location,
                                @RequestParam(required = false) Integer capacity,
                                @RequestParam(required = false) Long activityTypeId,
                                RedirectAttributes redirectAttributes) {
        try {
            Activity activity = activityService.getActivityById(id);
            if (activity == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "活动不存在！");
                return "redirect:/admin/activities/page";
            }

            // 更新活动信息
            activity.setTitle(title);
            activity.setDescription(description);
            activity.setLocation(location);
            activity.setCapacity(capacity);

            // 解析时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            if (startTime != null && !startTime.isEmpty()) {
                activity.setStartTime(dateFormat.parse(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                activity.setEndTime(dateFormat.parse(endTime));
            }

            // 设置活动类型
            if (activityTypeId != null) {
                ActivityType activityType = activityTypeService.getActivityTypeById(activityTypeId);
                activity.setActivityType(activityType);
            }

            // 保存活动
            activityService.updateActivity(id, activity);

            redirectAttributes.addFlashAttribute("successMessage", "活动更新成功！");
            return "redirect:/admin/activities/page";

        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "时间格式错误，请重新输入！");
            return "redirect:/admin/activities/edit/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "更新活动失败：" + e.getMessage());
            return "redirect:/admin/activities/edit/" + id;
        }
    }
    
    /**
     * 我的报名页面
     */
    @GetMapping("/user/my-registrations")
    public String myRegistrations(Model model) {
        model.addAttribute("title", "我的报名");
        return "user/my-registrations";
    }
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 欢迎页面 - 可作为备选首页
     */
    @GetMapping("/welcome")
    public String welcome(Model model) {
        List<Activity> recentActivities = activityService.getAllActivities();
        model.addAttribute("recentActivities", recentActivities);
        model.addAttribute("title", "欢迎页面");
        return "welcome";
    }

    /**
     * 活动大厅 - 可作为备选首页
     */
    @GetMapping("/hall")
    public String activityHall(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        model.addAttribute("activities", activities);
        model.addAttribute("title", "活动大厅");
        return "activity-hall";
    }
    /**
     * 单活动报名信息
     */

    @GetMapping("/admin/activities/{id}/registrations")
    public String registrations(@PathVariable Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            model.addAttribute("errorMessage", "活动不存在！");
            return "error/404"; // 或自定义一个友好错误页面
        }
        List<Registration> registrations = registrationService.getRegistrationsByActivityId(id);
        model.addAttribute("activity", activity);
        model.addAttribute("registrations", registrations);
        return "admin/activity-registrations";
    }
    /**
     * 统计报名信息
     */
    
    /**
     * 统计所有活动报名人数页面
     */
    @GetMapping("/admin/statistics")
    public String activityStatistics(Model model) {
        List<Activity> activities = activityService.getAllActivities();
        // 获取每个活动的报名人数统计
        java.util.Map<Long, Long> registrationCountMap = registrationService.countRegistrationsForAllActivities();
        model.addAttribute("activities", activities != null ? activities : new ArrayList<>());
        model.addAttribute("registrationCountMap", registrationCountMap != null ? registrationCountMap : new HashMap<>());
        model.addAttribute("title", "活动报名统计");
        return "admin/statistics";
    }

}
