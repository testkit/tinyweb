LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../..
LOCAL_MODULE := cgi-getfield
LOCAL_SRC_FILES := ../../../cgi-getfield.c
include $(BUILD_EXECUTABLE)


include $(CLEAR_VARS)
LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../..
LOCAL_MODULE := cgi-getcookie
LOCAL_SRC_FILES := ../../../cgi-getcookie.c
include $(BUILD_EXECUTABLE)

include $(CLEAR_VARS)
LOCAL_CFLAGS := -DUSE_WEBSOCKET
LOCAL_CFLAGS += -pie -fPIE
LOCAL_LDFLAGS += -pie -fPIE
LOCAL_C_INCLUDES += $(LOCAL_PATH)/../../..
LOCAL_MODULE := tinyweb
LOCAL_SRC_FILES := ../../../websocket.c ../../../mongoose.c ../../../tinyweb.c
include $(BUILD_EXECUTABLE)



