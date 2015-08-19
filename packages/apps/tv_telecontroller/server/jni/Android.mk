LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES := \
	writeevent.c
LOCAL_C_INCLUDES := \
	$(JNI_H_INCLUDE) \
LOCAL_SHARED_LIBRARIES := \
  liblog  \
	libcutils \
	libutils 

LOCAL_STATIC_LIBRARIES  :=   liblog libcutils 
LOCAL_MODULE := libwriteevent
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_TAGS := eng
include $(BUILD_SHARED_LIBRARY)

