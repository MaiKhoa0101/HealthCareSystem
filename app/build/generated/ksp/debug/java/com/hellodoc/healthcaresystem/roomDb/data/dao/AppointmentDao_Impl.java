package com.hellodoc.healthcaresystem.roomDb.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.hellodoc.healthcaresystem.roomDb.data.entity.AppointmentEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppointmentDao_Impl implements AppointmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppointmentEntity> __insertionAdapterOfAppointmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAppointments;

  private final SharedSQLiteStatement __preparedStmtOfClearPatientAppointments;

  private final SharedSQLiteStatement __preparedStmtOfClearDoctorAppointments;

  public AppointmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppointmentEntity = new EntityInsertionAdapter<AppointmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `appointments` (`id`,`patientId`,`doctorId`,`specialtyId`,`appointmentDate`,`appointmentTime`,`location`,`status`,`reason`,`notes`,`createdAt`,`updatedAt`,`doctorName`,`doctorAvatarUrl`,`patientName`,`specialtyName`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppointmentEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPatientId());
        statement.bindString(3, entity.getDoctorId());
        statement.bindString(4, entity.getSpecialtyId());
        statement.bindString(5, entity.getAppointmentDate());
        statement.bindString(6, entity.getAppointmentTime());
        statement.bindString(7, entity.getLocation());
        statement.bindString(8, entity.getStatus());
        if (entity.getReason() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getReason());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getNotes());
        }
        statement.bindString(11, entity.getCreatedAt());
        statement.bindString(12, entity.getUpdatedAt());
        if (entity.getDoctorName() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDoctorName());
        }
        if (entity.getDoctorAvatarUrl() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getDoctorAvatarUrl());
        }
        if (entity.getPatientName() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getPatientName());
        }
        if (entity.getSpecialtyName() == null) {
          statement.bindNull(16);
        } else {
          statement.bindString(16, entity.getSpecialtyName());
        }
      }
    };
    this.__preparedStmtOfClearAppointments = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM appointments";
        return _query;
      }
    };
    this.__preparedStmtOfClearPatientAppointments = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM appointments WHERE patientId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearDoctorAppointments = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM appointments WHERE doctorId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAppointments(final List<AppointmentEntity> appointments,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppointmentEntity.insert(appointments);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAppointments(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAppointments.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAppointments.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearPatientAppointments(final String patientId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearPatientAppointments.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, patientId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearPatientAppointments.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDoctorAppointments(final String doctorId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDoctorAppointments.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, doctorId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDoctorAppointments.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllAppointments(
      final Continuation<? super List<AppointmentEntity>> $completion) {
    final String _sql = "SELECT * FROM appointments";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppointmentEntity>>() {
      @Override
      @NonNull
      public List<AppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfSpecialtyId = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorAvatarUrl");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfSpecialtyName = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyName");
          final List<AppointmentEntity> _result = new ArrayList<AppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppointmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPatientId;
            _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            final String _tmpDoctorId;
            _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            final String _tmpSpecialtyId;
            _tmpSpecialtyId = _cursor.getString(_cursorIndexOfSpecialtyId);
            final String _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            final String _tmpAppointmentTime;
            _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            final String _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfDoctorAvatarUrl)) {
              _tmpDoctorAvatarUrl = null;
            } else {
              _tmpDoctorAvatarUrl = _cursor.getString(_cursorIndexOfDoctorAvatarUrl);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpSpecialtyName;
            if (_cursor.isNull(_cursorIndexOfSpecialtyName)) {
              _tmpSpecialtyName = null;
            } else {
              _tmpSpecialtyName = _cursor.getString(_cursorIndexOfSpecialtyName);
            }
            _item = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpDoctorId,_tmpSpecialtyId,_tmpAppointmentDate,_tmpAppointmentTime,_tmpLocation,_tmpStatus,_tmpReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDoctorName,_tmpDoctorAvatarUrl,_tmpPatientName,_tmpSpecialtyName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPatientAppointments(final String patientId,
      final Continuation<? super List<AppointmentEntity>> $completion) {
    final String _sql = "SELECT * FROM appointments WHERE patientId = ? ORDER BY appointmentDate DESC, appointmentTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, patientId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppointmentEntity>>() {
      @Override
      @NonNull
      public List<AppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfSpecialtyId = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorAvatarUrl");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfSpecialtyName = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyName");
          final List<AppointmentEntity> _result = new ArrayList<AppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppointmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPatientId;
            _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            final String _tmpDoctorId;
            _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            final String _tmpSpecialtyId;
            _tmpSpecialtyId = _cursor.getString(_cursorIndexOfSpecialtyId);
            final String _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            final String _tmpAppointmentTime;
            _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            final String _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfDoctorAvatarUrl)) {
              _tmpDoctorAvatarUrl = null;
            } else {
              _tmpDoctorAvatarUrl = _cursor.getString(_cursorIndexOfDoctorAvatarUrl);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpSpecialtyName;
            if (_cursor.isNull(_cursorIndexOfSpecialtyName)) {
              _tmpSpecialtyName = null;
            } else {
              _tmpSpecialtyName = _cursor.getString(_cursorIndexOfSpecialtyName);
            }
            _item = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpDoctorId,_tmpSpecialtyId,_tmpAppointmentDate,_tmpAppointmentTime,_tmpLocation,_tmpStatus,_tmpReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDoctorName,_tmpDoctorAvatarUrl,_tmpPatientName,_tmpSpecialtyName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDoctorAppointments(final String doctorId,
      final Continuation<? super List<AppointmentEntity>> $completion) {
    final String _sql = "SELECT * FROM appointments WHERE doctorId = ? ORDER BY appointmentDate DESC, appointmentTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, doctorId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AppointmentEntity>>() {
      @Override
      @NonNull
      public List<AppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfSpecialtyId = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyId");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorAvatarUrl");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfSpecialtyName = CursorUtil.getColumnIndexOrThrow(_cursor, "specialtyName");
          final List<AppointmentEntity> _result = new ArrayList<AppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppointmentEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPatientId;
            _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            final String _tmpDoctorId;
            _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            final String _tmpSpecialtyId;
            _tmpSpecialtyId = _cursor.getString(_cursorIndexOfSpecialtyId);
            final String _tmpAppointmentDate;
            _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            final String _tmpAppointmentTime;
            _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            if (_cursor.isNull(_cursorIndexOfReason)) {
              _tmpReason = null;
            } else {
              _tmpReason = _cursor.getString(_cursorIndexOfReason);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            final String _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getString(_cursorIndexOfUpdatedAt);
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfDoctorAvatarUrl)) {
              _tmpDoctorAvatarUrl = null;
            } else {
              _tmpDoctorAvatarUrl = _cursor.getString(_cursorIndexOfDoctorAvatarUrl);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpSpecialtyName;
            if (_cursor.isNull(_cursorIndexOfSpecialtyName)) {
              _tmpSpecialtyName = null;
            } else {
              _tmpSpecialtyName = _cursor.getString(_cursorIndexOfSpecialtyName);
            }
            _item = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpDoctorId,_tmpSpecialtyId,_tmpAppointmentDate,_tmpAppointmentTime,_tmpLocation,_tmpStatus,_tmpReason,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDoctorName,_tmpDoctorAvatarUrl,_tmpPatientName,_tmpSpecialtyName);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
