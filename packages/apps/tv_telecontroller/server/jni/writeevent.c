#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
#include <sys/time.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <jni.h>
#include <utils/Log.h>

#define LOG_TAG "writeevent"
//struct input_event ev;
//#define DEVICE_NAME "/dev/input/event1"
struct mouse_event{
	int flag;
	int x;
	int y;
};
int fd=-1;
int tempx=640;
int tempy=360;
float b[3]={1,2,3};
int i=0;


struct mouse_event event;


jint Java_com_android_systemui_myservice_MyLib_openvmouse(JNIEnv *env,jobject jobj){

	fd=open("/dev/vmouse",O_RDWR);
	if(fd<0){
		ALOGD("open /dev/vmouse fail\n");
		return -1;
	}
	return 0;
}
jint Java_com_android_systemui_myservice_MyLib_writegsensor(JNIEnv *env,jobject jobj,jfloatArray floatArry){

	jfloat* pf=(*env)->GetFloatArrayElements(env,floatArry,0);
	jsize size=(*env)->GetArrayLength(env,floatArry);

	memcpy(b,pf,sizeof(b));
	
	
	//ALOGD("b[0]=%f,b[1]=%f,b[2]=%f\n",b[0],b[1],b[2]);
	
	FILE *fp;
	fp=fopen("/system/usr/hello.txt","w+");
	for(i=0;i<3;i++){
		fprintf(fp,"%f\n",b[i]);
	}
	fclose(fp);


	return 0;
}

jint Java_com_android_systemui_myservice_MyLib_writeevent(JNIEnv *env,jobject jobj,jintArray intArray){
	jint* p=(*env)->GetIntArrayElements(env,intArray,0);
	jsize size=(*env)->GetArrayLength(env,intArray);
	
	//ALOGD("p[0]=%d,P[1]=%d,p[2]=%d\n",p[0],p[1],p[2]);
	//test
	
	fd=open("/dev/vmouse",O_RDWR);
	if(fd<0) ALOGD("open /dev/vmouse fail\n");
	
	int flag=2;//0-BTN_RIGHT;1-BTN_MOUSE;2-REL-xy;3-REL_WHEEL;3-click
	/*event.flag=flag;
	event.x=p[0];
	event.y=p[1];

	int ret=write(fd,&event,sizeof(event));
	if(ret=-1) {
		ALOGD("write event fail\n");
	}*/
	//p[2]:0--down;1--move;2--up;
	
	switch(p[2]){
	case 0:
		
		tempx=p[0];
		tempy=p[1];
		/*
		event.flag=1;
		event.x=1;
		write(fd,&event,sizeof(event));
		
		event.flag=1;
		event.x=0;
		write(fd,&event,sizeof(event));
		*/
		
		
		
		break;
	case 3://click
		
		event.flag=1;
		event.x=1;
		write(fd,&event,sizeof(event));
		
		event.flag=1;
		event.x=0;
		write(fd,&event,sizeof(event));
		
		break;
	case 1://move
		event.flag=2;
		event.x=(p[0]-tempx);
		event.y=(p[1]-tempy);
		write(fd,&event,sizeof(event));
		tempx=p[0];
		tempy=p[1];
		
		break;
	case 10://mouse-left
		event.flag=1;
		event.x=1;
		write(fd,&event,sizeof(event));

		event.flag=1;
		event.x=0;
		write(fd,&event,sizeof(event));
		break;
	case 11://mouse-right
		event.flag=0;
		event.x=1;
		write(fd,&event,sizeof(event));

		event.flag=0;
		event.x=0;
		write(fd,&event,sizeof(event));
		break;
		

	}
	close(fd);
	
	return 0;
}

jint Java_com_android_systemui_myservice_MyLib_closevmouse(JNIEnv *env,jobject jobj){
		if(fd!=NULL){
			close(fd);
			fd=NULL;
		}
		return 0;
}
/*
jfloatArray Java_android_hardware_gsensorlib_getxyz(JNIEnv *env,jobject jobj){

	jfloatArray floatArray = (*env)->NewFloatArray(env,3);
	jfloat* pxyz=(*env)->GetFloatArrayElements(env,floatArray,0);

	//test
	//ALOGD("getexyz==b[0]=%f,b[1]=%f,b[2]=%f\n",b[0],b[1],b[2]);
	
	float temp[3]={0,0,0};
	FILE *fp=NULL;
	
	fp=fopen("/mnt/sdcard/hello.txt","r");
	if(fp!=NULL){
		for(i=0;i<3;i++){
			fscanf(fp,"%f\n",&temp[i]);
		}
	}else{
		ALOGD("read gsensor data err\n");
	}
	fclose(fp);
	for(i=0;i<3;i++) pxyz[i]=temp[i];
	
	ALOGD("pxyz[0]=%f,pxyz[1]=%f,pxyz[2]=%f\n",pxyz[0],pxyz[1],pxyz[2]);

	return floatArray;

			
}*/



