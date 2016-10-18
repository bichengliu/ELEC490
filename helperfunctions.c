#include "helperfunctions.h"
float find_mean(float sound_data[],int size)
{
	int i;
	float sum = 0;
	for( i = 0; i < size; i++)
	{
		sum = sum + sound_data[i];
		
	}
	return sum/size;
}

float find_var(float mean_array[], int size)
{
	float var;
	float mean = find_mean(mean_array[]);
	float sum = 0;
	int i;
	
	for(i = 0; i < size; i++)
	{
		sum = sum + mean_array[i]*mean_array[i];
	}
	return (sum/size - mean*mean);
}

void  change_mean_array(float mean_array[], int *oldest, float newest, int size)
{
	mean_array[oldest] = newest;
	*oldest++;
	if(*oldest == size)
		*oldest = 0;
}