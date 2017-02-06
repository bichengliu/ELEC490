% things that may need to change
bin_size = 2000;
delay_size = 2040; % to account for our 9e-4 second computing time
bin_count = 50;
% unchanging variables
Fs = 44100; 
sample_count = 0;
total_mean = 0;
mean_array = zeros(1,bin_count);
system_mean = 0;
system_std = 0;
forgetfulness = 0; % in case we want something more dynamic
[y, Fs] = audioread('Recording.m4a');

%{
% main calculation operation
total_energy = zeros(1,129); % some MATLAB default, subject to change
average_power = total_energy;
for i = 1:floor(length(y)/bin_size)

    x = y((i-1)*bin_size + 1:i*bin_size);
    
    
    [power_in_bin, frequency] = periodogram(x); % defaults length to 129 but we will keep it general
    
    
    sample_count = sample_count + 1;
    total_energy = total_energy + power_in_bin;
    average_power = total_energy/sample_count;
    
    for ll = 1:129 % need more time to figure out how to make decision
        if(power_in_bin(ll) > average_power(ll))
            output
            
        else
            
        end
    end
end
%}

for i = 1:floor(length(y)/delay_size)
    tic;
     sample_count = sample_count + 1;
     
     x_tot = y((i-1)*delay_size + 1:i*delay_size);
     x = x_tot(1:2000);
     abs_x = abs(x);
     %mean_x = mean(abs_x);
     mean_x = rms(x);
     
     %maybe fix to use indices instead of an array so that we can
     %remove/add more easily
     for l = 1:bin_count-1
         mean_array(l) = mean_array(l+1);
     end
     mean_array(bin_count) = mean_x; % other shifting methods
     
     system_mean = mean(mean_array);
     
     system_std = std(mean_array);
     
     threshold = 1.80*system_std + system_mean; % confidence interval subject to change
     threshold_array((i-1)*bin_size + 1:i*bin_size) = threshold*ones(1,bin_size);
     average_time_elapsed(sample_count) = toc;
     if(mean_x > threshold)
         outp_array((i-1)*bin_size + 1:i*bin_size) = x; % what we would play dynamically
         outp_array(i*bin_size+1:i*bin_size+40) = zeros(1,40);
     else
         outp_array((i-1)*bin_size + 1:i*bin_size) = zeros(1,bin_size); % this is just for testing, because we are writing to a file
         outp_array(i*bin_size+1:i*bin_size+40) = zeros(1,40);
     end
     toc;

end

figure;
hold on;
plot(y);
xlabel('Time');
ylabel('Ampltude');

figure;
hold on;
plot(outp_array);
xlabel('Time');
ylabel('Ampltude');

figure;
hold on;
plot(y);
plot(threshold_array,'r');
xlabel('Time');
ylabel('Ampltude');
audiowrite('outp.mp4',outp_array,Fs);