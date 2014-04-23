################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
# -include sources.mk
O_SRCS := 
PO_SRCS := 
CPP_SRCS := 
C_UPPER_SRCS := 
C_SRCS := 
S_UPPER_SRCS := 
OBJ_SRCS := 
EDC_SRCS := 
ASM_SRCS := 
CXX_SRCS := 
C++_SRCS := 
CC_SRCS := 
MO_FILES := 
OBJS := 
C++_DEPS := 
C_DEPS := 
CC_DEPS := 
EDJ_FILES := 
CPP_DEPS := 
EXECUTABLES := 
C_UPPER_DEPS := 
CXX_DEPS := 

# Every subdirectory with source files must be described here
SUBDIRS := \
source \
../mongoose.c \
../websocket.c \
../webservice.c \
../tinyweb.c \
../cgi-getfield.c \
../cgi-getcookie.c

CFLAGS=	-I.. -pthread -g -fpermissive
TINY_OBJS = \
./source/mongoose.o \
./source/websocket.o \
./source/webservice.o \
./source/tinyweb.o
COOKIE_OBJS = ./source/cgi-getcookie.o
FIELD_OBJS = ./source/cgi-getfield.o

#CPP_DEPS += \
./source/mongoose.d \
./source/websocket.d \
./source/webservice.d \
./source/tinyweb.d \
./source/cgi-getcookie.d \
./source/cgi-getfield.d


# Each subdirectory must supply rules for building sources it contributes
source/%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: C++ Compiler'
	$(CC) -I"pch" -D_DEBUG -O0 -g3 -c -fmessage-length=0 $(INCLUDE_PATH) -I$(SDKPATH)/library -I$(ROOTSTRAP)/usr/include $(CFLAGS) -DUSE_WEBSOCKET -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


# -include objects.mk
USER_OBJS :=

LIBS :=

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: tinyweb secondary-outputs

init: 
	mkdir -p source/
	cp ../server.pem .

# Tool invocations
tinyweb: init $(TINY_OBJS) $(COOKIE_OBJS) $(FIELD_OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: C++ Linker'
	$(CC) -o"echo.so" -shared -fPIC -L$(ROOTSTRAP)/usr/lib --sysroot=$(ROOTSTRAP) $(USER_OBJS) $(LIBS) $(CFLAGS) -L./ ../echo.c
	$(CC) -o"libmongoose.so" -shared -fPIC -L$(ROOTSTRAP)/usr/lib -ldl --sysroot=$(ROOTSTRAP) $(USER_OBJS) $(LIBS) $(CFLAGS) -DUSE_WEBSOCKET ../mongoose.c
	$(CC) -o"tinyweb" -L$(ROOTSTRAP)/usr/lib --sysroot=$(ROOTSTRAP) $(TINY_OBJS) $(USER_OBJS) $(LIBS) $(CFLAGS) -ldl
	$(CC) -o"cgi-getcookie" -L$(ROOTSTRAP)/usr/lib --sysroot=$(ROOTSTRAP) $(COOKIE_OBJS) $(USER_OBJS) $(CFLAGS)
	$(CC) -o"cgi-getfield" -L$(ROOTSTRAP)/usr/lib --sysroot=$(ROOTSTRAP) $(FIELD_OBJS) $(USER_OBJS) $(CFLAGS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) source tinyweb cgi-getcookie cgi-getfield *.so server.pem

secondary-outputs: $(MO_FILES) $(EDJ_FILES)

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets

