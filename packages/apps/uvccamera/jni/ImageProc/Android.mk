LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES := \
	ImageProc.c
	
LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE) \
	
LOCAL_SHARED_LIBRARIES := \
  liblog  \
	libcutils \
	libutils 

LOCAL_STATIC_LIBRARIES  :=   liblog libcutils 
LOCAL_SHARED_LIBRARIES := libdl liblog libjnigraphics
LOCAL_LDLIBS := -ljnigraphics
LOCAL_MODULE := libImageProc

include $(BUILD_SHARED_LIBRARY)
