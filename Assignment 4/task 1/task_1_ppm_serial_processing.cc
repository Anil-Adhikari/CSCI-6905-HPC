/***
To execute this program
Compile: g++ -o task_1_ppm_serial_processing task_1_ppm_serial_processing.cc
Run: ./task_1_ppm_serial_processing ttu_tile.ppm ttu_tile_grayed_flipped.ppm
***/

#include <fstream>
#include <stdexcept>
#include <sstream>
#include <iostream>
#include "libppm.h"
#include <sys/time.h>

int main(int argc, char ** argv){
	
	// checks for the arguments supplied from the command line
	if (argc != 3) {
		std::cerr << "Usage: " << argv[0] << " in_ppm_file out_ppm_file" <<
		std::endl;
		return 1;
	}

	std::ifstream inputImage;
	std::ofstream outputImage;
	PPM_header inputHeader;

	inputImage.open(argv[1], std::ios::binary);

	if(!inputImage){
		std::cout << "\n";
		std::cout << "PPM_WRITE: Fatal error! \n";
		std::cout << "Cannot open the input file " << argv[1] << ".\n";
		return -1;
	}

	outputImage.open(argv[2], std::ios::binary);

	if ( !outputImage )
  	{
	    std::cout << "\n";
	    std::cout << "PPMB_WRITE: Fatal error!\n";
	    std::cout << "Cannot open the output file " << argv[2] << ".\n";
	    return -1;
  	}

  	// method call to read header file for the input ppm image
	PPM_read_header(inputImage, inputHeader);
	std::cout << inputHeader;

	// declaring a pointer to an RGB_8 and using the new operator to allocate it
	// this pointer is used to store rgb value of each pixel in the image
	RGB_8 * image = new RGB_8[inputHeader.width * inputHeader.height];

	// method call to get the RGB value of the input image
	PPM_read_rgb_8(inputImage, inputHeader.width, inputHeader.height, image);

	// This block deals with printing the execution time
	struct timeval tv1;
	struct timeval tv2;
	gettimeofday(&tv1, NULL);

	// method call to convert image into gray scale and 
	// flip image
	to_grayscale(image, inputHeader.width, inputHeader.height);
	flip(image, inputHeader.width, inputHeader.height);

	gettimeofday(&tv2, NULL);

	std::cout << "Total time: " << (double) (tv2.tv_usec - tv1.tv_usec) / 1000000
	+ (double) (tv2.tv_sec - tv1.tv_sec) << std::endl;

	// write header and image
	PPM_write_header_8(outputImage, inputHeader.width, inputHeader.height);
	PPM_write_rgb_8(outputImage, inputHeader.width, inputHeader.height, image);

	return 0;
}

// This method reads header of the input image
void PPM_read_header(std::ifstream &inp, PPM_header &ppm_header) {
	char ppm_magic_1, ppm_magic_2;
	
	inp >> ppm_magic_1;
	inp >> ppm_magic_2;
	
	if (ppm_magic_1 != PPM_MAGIC_1 || ppm_magic_2 != PPM_MAGIC_2) {
		throw std::runtime_error("File does not begin with PPM magic number");
	}
	
	int width;
	inp >> width;
	ppm_header.width = width;
	int height;
	inp >> height;
	ppm_header.height = height;

	int max_color;
	inp >> max_color;
	ppm_header.max_color = max_color;

	char space;
	inp.read(&space, 1);

	return;
}

// This method prints the RGB value of the image
std::ostream &operator<<(std::ostream &os, const RGB_8 &rgb) {
	os << (int) rgb.r << " " << (int) rgb.g << " " << (int) rgb.b;
	return os;
}

// This method reads RGB value of the image
void PPM_read_rgb_8(std::ifstream &inp, int width, int height, RGB_8 *img) {
  inp.read((char *)img, sizeof(RGB_8)*width*height);
  if (!inp) {
    std::stringstream ss;
    ss << "error: only " << inp.gcount() << " could be read";
    throw std::runtime_error(ss.str());		
  }
  
}

// This method writes header of the image
void PPM_write_header_8(std::ofstream &outp, int width, int height) {
  	// write the header	
  	outp << PPM_MAGIC_1 << PPM_MAGIC_2 << " " << width << " " << height << " "<< 255 << " " << std::endl;
}

// This method writes the image
void PPM_write_rgb_8(std::ofstream &outp, int width, int height, RGB_8 *img) {	
	// write the image
	outp.write((char *)img, sizeof(RGB_8) * width * height);
	if (!outp) {
	  std::stringstream ss;
	  ss << "error: only " << outp.tellp() << " could be written";
	  throw std::runtime_error(ss.str());		
	}

}

// This method prints the dimension info of the input image
std::ostream &operator<<(std::ostream &os, const PPM_header &header) {
	os << "Width: " << header.width << " Height: " << header.height << " Max color: " << header.max_color << std::endl;
	return os;
}

// This method converts the input image into gray scale
void to_grayscale(RGB_8 * image, int width, int height){
	float temp;
	for(int i = 0; i < (width * height); i++){
		temp = 0.21 * image[i].r + 0.72 * image[i].g + 0.07 * image[i].b;
		image[i].r = temp;
		image[i].g = temp;
		image[i].b = temp;
	}
	
}

// This method flips the input image
void flip(RGB_8 * image, int width, int height){
	for(int row = 0; row < height; row++){
		for(int col = 0; col < width/2; col++){
			RGB_8 temp;
			temp = image[row*width+col];
			image[row*width+col] = image[(row+1)*width-col-1];
			image[(row+1)*width-col-1] = temp;
		}
	}
}
