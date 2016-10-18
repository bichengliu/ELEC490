#ifndef HELPER_FUNCTIONS 
#define HELPER_FUNCTIONS
float find_mean(float sound_data[],int size); // we know the lengths of these
float find_var(float mean_array[], int size); // we know the lengths of these
void  change_mean_array(float mean_array[], int oldest, float newest, int size);
#endif