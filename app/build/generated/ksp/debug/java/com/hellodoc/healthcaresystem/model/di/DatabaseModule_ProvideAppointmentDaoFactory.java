package com.hellodoc.healthcaresystem.model.di;

import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao;
import com.hellodoc.healthcaresystem.roomDb.data.database.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class DatabaseModule_ProvideAppointmentDaoFactory implements Factory<AppointmentDao> {
  private final Provider<AppDatabase> databaseProvider;

  private DatabaseModule_ProvideAppointmentDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AppointmentDao get() {
    return provideAppointmentDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideAppointmentDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideAppointmentDaoFactory(databaseProvider);
  }

  public static AppointmentDao provideAppointmentDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAppointmentDao(database));
  }
}
