% things that may need to change

ind_bin_length = 2000;
bin_count = 5;
bin_size = bin_count * ind_bin_length;
fft_size = 64;
energy_sum = zeros(1,fft_size);
alpha = 2.35;
beta_param = 1.02;
total_crunch = 0;
limit = 200;
sample_count = 0;
% unchanging variables
Fs = 44100; 
[y, Fs] = audioread('Recording.m4a');
for i = 1:floor(length(y)/bin_size)


    x = y((i-1)*bin_size + 1:i*bin_size);
    
    
    if(total_crunch > limit)
        total_crunch = 1;
        energy_sum = energy_mean;
    end
    
    for j = 1:bin_count
        tic;
        sample_count = sample_count + 1;

        xx = x((j-1)*ind_bin_length + 1:j*ind_bin_length);
        xx_fft = fft(xx,fft_size);
        xx_nrg = abs(xx_fft).^2;
        total_crunch = total_crunch + 1;
        energy_sum = energy_sum + xx_nrg;
        energy_mean = energy_sum/total_crunch;

        if(sum(xx_nrg > alpha*energy_mean) > floor(fft_size/32))
            outp_array((i-1)*bin_size+1+(j-1)*ind_bin_length:(i-1)*bin_size+j*ind_bin_length) = xx;
        else
            outp_array((i-1)*bin_size+1+(j-1)*ind_bin_length:(i-1)*bin_size+j*ind_bin_length) = zeros(1,ind_bin_length);
        end
        avg_time_elapsed(sample_count) = toc;
    end

end


figure;
hold on;
plot(outp_array);
xlabel('time');
ylabel('amplitude');
title('Output of Algorithm Visualized as a Soundwave');

figure;
hold on;
plot(y);
title('Original Soundfile');
xlabel('time');
ylabel('amplitude');
audiowrite('outp.mp4',outp_array,Fs);

relative_mean_runtime = mean(avg_time_elapsed)/mean(average_time_elapsed)
relative_var_runtime = var(avg_time_elapsed)/var(average_time_elapsed)