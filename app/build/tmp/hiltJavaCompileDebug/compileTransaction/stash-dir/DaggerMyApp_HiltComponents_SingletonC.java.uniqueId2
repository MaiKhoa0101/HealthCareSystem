package com.hellodoc.healthcaresystem.view.user.home.root;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hellodoc.healthcaresystem.api.AdminService;
import com.hellodoc.healthcaresystem.api.AppointmentService;
import com.hellodoc.healthcaresystem.api.AuthService;
import com.hellodoc.healthcaresystem.api.DoctorService;
import com.hellodoc.healthcaresystem.api.FAQItemService;
import com.hellodoc.healthcaresystem.api.GeminiService;
import com.hellodoc.healthcaresystem.api.MedicalOptionService;
import com.hellodoc.healthcaresystem.api.NewsService;
import com.hellodoc.healthcaresystem.api.NotificationService;
import com.hellodoc.healthcaresystem.api.PostService;
import com.hellodoc.healthcaresystem.api.ReportService;
import com.hellodoc.healthcaresystem.api.ReviewService;
import com.hellodoc.healthcaresystem.api.SpecialtyService;
import com.hellodoc.healthcaresystem.api.UserService;
import com.hellodoc.healthcaresystem.model.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.hellodoc.healthcaresystem.model.di.DatabaseModule_ProvideAppointmentDaoFactory;
import com.hellodoc.healthcaresystem.model.repository.AppointmentRepository;
import com.hellodoc.healthcaresystem.model.repository.DoctorRepository;
import com.hellodoc.healthcaresystem.model.repository.FAQItemRepository;
import com.hellodoc.healthcaresystem.model.repository.GeminiRepository;
import com.hellodoc.healthcaresystem.model.repository.MedicalOptionRepository;
import com.hellodoc.healthcaresystem.model.repository.NewsRepository;
import com.hellodoc.healthcaresystem.model.repository.NotificationRepository;
import com.hellodoc.healthcaresystem.model.repository.PostRepository;
import com.hellodoc.healthcaresystem.model.repository.ReportRepository;
import com.hellodoc.healthcaresystem.model.repository.ReviewRepository;
import com.hellodoc.healthcaresystem.model.repository.SpecialtyRepository;
import com.hellodoc.healthcaresystem.model.repository.UserRepository;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideAdminServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideAppointmentServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideAuthServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideDoctorServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideFAQItemServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideGeminiServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideMedicalOptionServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideNewsServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideNotificationServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideOkHttpClientFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvidePostServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideReportServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideRetrofitFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideReviewServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideSpecialtyServiceFactory;
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance_ProvideUserServiceFactory;
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao;
import com.hellodoc.healthcaresystem.roomDb.data.database.AppDatabase;
import com.hellodoc.healthcaresystem.view.admin.AdminRoot;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.ForgotPasswordActivity;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.Intro1;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.Intro2;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.Intro3;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.ResetPasswordActivity;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.ResetPasswordSuccessActivity;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SecondSignUp;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignIn;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignUp;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignUpSuccess;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.StartScreen;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.VerifyOtpActivity;
import com.hellodoc.healthcaresystem.view.user.home.startscreen.VerifyOtpSignUpAcctivity;
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel;
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel;
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel;
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel;
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel;
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel;
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.NewsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel;
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel;
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel;
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel;
import com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel;
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel;
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel_HiltModules;
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DaggerMyApp_HiltComponents_SingletonC {
  private DaggerMyApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MyApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MyApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MyApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MyApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MyApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MyApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MyApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MyApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MyApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MyApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MyApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MyApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MyApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MyApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectAdminRoot(AdminRoot adminRoot) {
    }

    @Override
    public void injectHomeActivity(HomeActivity homeActivity) {
    }

    @Override
    public void injectForgotPasswordActivity(ForgotPasswordActivity forgotPasswordActivity) {
    }

    @Override
    public void injectIntro1(Intro1 intro1) {
    }

    @Override
    public void injectIntro2(Intro2 intro2) {
    }

    @Override
    public void injectIntro3(Intro3 intro3) {
    }

    @Override
    public void injectResetPasswordActivity(ResetPasswordActivity resetPasswordActivity) {
    }

    @Override
    public void injectResetPasswordSuccessActivity(
        ResetPasswordSuccessActivity resetPasswordSuccessActivity) {
    }

    @Override
    public void injectSecondSignUp(SecondSignUp secondSignUp) {
    }

    @Override
    public void injectSignIn(SignIn signIn) {
    }

    @Override
    public void injectSignUpSuccess(SignUpSuccess signUpSuccess) {
    }

    @Override
    public void injectSignUp(SignUp signUp) {
    }

    @Override
    public void injectStartScreen(StartScreen startScreen) {
    }

    @Override
    public void injectVerifyOtpActivity(VerifyOtpActivity verifyOtpActivity) {
    }

    @Override
    public void injectVerifyOtpSignUpAcctivity(VerifyOtpSignUpAcctivity verifyOtpSignUpAcctivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(12).put(AppointmentViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AppointmentViewModel_HiltModules.KeyModule.provide()).put(DoctorViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DoctorViewModel_HiltModules.KeyModule.provide()).put(FAQItemViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FAQItemViewModel_HiltModules.KeyModule.provide()).put(GeminiViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, GeminiViewModel_HiltModules.KeyModule.provide()).put(MedicalOptionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MedicalOptionViewModel_HiltModules.KeyModule.provide()).put(NewsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, NewsViewModel_HiltModules.KeyModule.provide()).put(NotificationViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, NotificationViewModel_HiltModules.KeyModule.provide()).put(PostViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PostViewModel_HiltModules.KeyModule.provide()).put(ReportViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ReportViewModel_HiltModules.KeyModule.provide()).put(ReviewViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ReviewViewModel_HiltModules.KeyModule.provide()).put(SpecialtyViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SpecialtyViewModel_HiltModules.KeyModule.provide()).put(UserViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, UserViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends MyApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AppointmentViewModel> appointmentViewModelProvider;

    Provider<DoctorViewModel> doctorViewModelProvider;

    Provider<FAQItemViewModel> fAQItemViewModelProvider;

    Provider<GeminiViewModel> geminiViewModelProvider;

    Provider<MedicalOptionViewModel> medicalOptionViewModelProvider;

    Provider<NewsViewModel> newsViewModelProvider;

    Provider<NotificationViewModel> notificationViewModelProvider;

    Provider<PostViewModel> postViewModelProvider;

    Provider<ReportViewModel> reportViewModelProvider;

    Provider<ReviewViewModel> reviewViewModelProvider;

    Provider<SpecialtyViewModel> specialtyViewModelProvider;

    Provider<UserViewModel> userViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    AppointmentRepository appointmentRepository() {
      return new AppointmentRepository(singletonCImpl.provideAppointmentDaoProvider.get(), singletonCImpl.appointmentService());
    }

    DoctorRepository doctorRepository() {
      return new DoctorRepository(singletonCImpl.doctorService());
    }

    FAQItemRepository fAQItemRepository() {
      return new FAQItemRepository(singletonCImpl.fAQItemService());
    }

    PostRepository postRepository() {
      return new PostRepository(singletonCImpl.postService());
    }

    MedicalOptionRepository medicalOptionRepository() {
      return new MedicalOptionRepository(singletonCImpl.medicalOptionService());
    }

    NewsRepository newsRepository() {
      return new NewsRepository(singletonCImpl.newsService());
    }

    NotificationRepository notificationRepository() {
      return new NotificationRepository(singletonCImpl.notificationService());
    }

    GeminiRepository geminiRepository() {
      return new GeminiRepository(singletonCImpl.geminiService());
    }

    ReviewRepository reviewRepository() {
      return new ReviewRepository(singletonCImpl.reviewService());
    }

    SpecialtyRepository specialtyRepository() {
      return new SpecialtyRepository(singletonCImpl.specialtyService());
    }

    UserRepository userRepository() {
      return new UserRepository(singletonCImpl.userService(), singletonCImpl.adminService(), singletonCImpl.authService());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.appointmentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.doctorViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.fAQItemViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.geminiViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.medicalOptionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.newsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.notificationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.postViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.reportViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.reviewViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.specialtyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.userViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(12).put(AppointmentViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (appointmentViewModelProvider))).put(DoctorViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (doctorViewModelProvider))).put(FAQItemViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (fAQItemViewModelProvider))).put(GeminiViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (geminiViewModelProvider))).put(MedicalOptionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (medicalOptionViewModelProvider))).put(NewsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (newsViewModelProvider))).put(NotificationViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (notificationViewModelProvider))).put(PostViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (postViewModelProvider))).put(ReportViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (reportViewModelProvider))).put(ReviewViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (reviewViewModelProvider))).put(SpecialtyViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (specialtyViewModelProvider))).put(UserViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (userViewModelProvider))).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
          return (T) new AppointmentViewModel(viewModelCImpl.appointmentRepository());

          case 1: // com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
          return (T) new DoctorViewModel(viewModelCImpl.doctorRepository(), viewModelCImpl.appointmentRepository());

          case 2: // com.hellodoc.healthcaresystem.viewmodel.FAQItemViewModel
          return (T) new FAQItemViewModel(viewModelCImpl.fAQItemRepository());

          case 3: // com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
          return (T) new GeminiViewModel(viewModelCImpl.postRepository(), viewModelCImpl.doctorRepository());

          case 4: // com.hellodoc.healthcaresystem.viewmodel.MedicalOptionViewModel
          return (T) new MedicalOptionViewModel(viewModelCImpl.medicalOptionRepository());

          case 5: // com.hellodoc.healthcaresystem.viewmodel.NewsViewModel
          return (T) new NewsViewModel(viewModelCImpl.newsRepository());

          case 6: // com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel
          return (T) new NotificationViewModel(viewModelCImpl.notificationRepository());

          case 7: // com.hellodoc.healthcaresystem.viewmodel.PostViewModel
          return (T) new PostViewModel(viewModelCImpl.postRepository(), viewModelCImpl.geminiRepository());

          case 8: // com.hellodoc.healthcaresystem.viewmodel.ReportViewModel
          return (T) new ReportViewModel(singletonCImpl.reportRepositoryProvider.get());

          case 9: // com.hellodoc.healthcaresystem.viewmodel.ReviewViewModel
          return (T) new ReviewViewModel(viewModelCImpl.reviewRepository());

          case 10: // com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
          return (T) new SpecialtyViewModel(viewModelCImpl.specialtyRepository());

          case 11: // com.hellodoc.healthcaresystem.viewmodel.UserViewModel
          return (T) new UserViewModel(viewModelCImpl.userRepository());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MyApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MyApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MyApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<AppDatabase> provideAppDatabaseProvider;

    Provider<AppointmentDao> provideAppointmentDaoProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<Retrofit> provideRetrofitProvider;

    Provider<ReportRepository> reportRepositoryProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    AppointmentService appointmentService() {
      return RetrofitInstance_ProvideAppointmentServiceFactory.provideAppointmentService(provideRetrofitProvider.get());
    }

    DoctorService doctorService() {
      return RetrofitInstance_ProvideDoctorServiceFactory.provideDoctorService(provideRetrofitProvider.get());
    }

    FAQItemService fAQItemService() {
      return RetrofitInstance_ProvideFAQItemServiceFactory.provideFAQItemService(provideRetrofitProvider.get());
    }

    PostService postService() {
      return RetrofitInstance_ProvidePostServiceFactory.providePostService(provideRetrofitProvider.get());
    }

    MedicalOptionService medicalOptionService() {
      return RetrofitInstance_ProvideMedicalOptionServiceFactory.provideMedicalOptionService(provideRetrofitProvider.get());
    }

    NewsService newsService() {
      return RetrofitInstance_ProvideNewsServiceFactory.provideNewsService(provideRetrofitProvider.get());
    }

    NotificationService notificationService() {
      return RetrofitInstance_ProvideNotificationServiceFactory.provideNotificationService(provideRetrofitProvider.get());
    }

    GeminiService geminiService() {
      return RetrofitInstance_ProvideGeminiServiceFactory.provideGeminiService(provideRetrofitProvider.get());
    }

    ReportService reportService() {
      return RetrofitInstance_ProvideReportServiceFactory.provideReportService(provideRetrofitProvider.get());
    }

    ReviewService reviewService() {
      return RetrofitInstance_ProvideReviewServiceFactory.provideReviewService(provideRetrofitProvider.get());
    }

    SpecialtyService specialtyService() {
      return RetrofitInstance_ProvideSpecialtyServiceFactory.provideSpecialtyService(provideRetrofitProvider.get());
    }

    UserService userService() {
      return RetrofitInstance_ProvideUserServiceFactory.provideUserService(provideRetrofitProvider.get());
    }

    AdminService adminService() {
      return RetrofitInstance_ProvideAdminServiceFactory.provideAdminService(provideRetrofitProvider.get());
    }

    AuthService authService() {
      return RetrofitInstance_ProvideAuthServiceFactory.provideAuthService(provideRetrofitProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.provideAppointmentDaoProvider = DoubleCheck.provider(new SwitchingProvider<AppointmentDao>(singletonCImpl, 0));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 3));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 2));
      this.reportRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ReportRepository>(singletonCImpl, 4));
    }

    @Override
    public void injectMyApp(MyApp myApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
          return (T) DatabaseModule_ProvideAppointmentDaoFactory.provideAppointmentDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 1: // com.hellodoc.healthcaresystem.roomDb.data.database.AppDatabase
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // retrofit2.Retrofit
          return (T) RetrofitInstance_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 3: // okhttp3.OkHttpClient
          return (T) RetrofitInstance_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 4: // com.hellodoc.healthcaresystem.model.repository.ReportRepository
          return (T) new ReportRepository(singletonCImpl.reportService());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
