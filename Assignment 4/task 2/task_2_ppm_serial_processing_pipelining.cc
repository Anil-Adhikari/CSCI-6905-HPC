/***
To execute this proram:
Compile: g++ -o task_2_ppm_serial_processing_pipelining task_2_ppm_serial_processing_pipelining.cc
Run: ./task_2_ppm_serial_processing_pipelining ttu_tile.ppm ttu_tile_grayed_flipped.ppm
***/

#include <iostream>
#include <fstream>
#include <stdexcept>
#include <sstream>
#include <sys/time.h>
#include <queue>

#include "libppm.h"

using namespace std;

std::queue<RGB_8 *> pipeline;

int main(int argc, char *argv[]) {
  
  // checks for the arguments supplied from the command line
  if (argc != 3) {
    std::cerr << "Usage: " << argv[0] << " in_ppm_file out_ppm_file" << std::endl;
    return 1;
  }
  
  PPM_header img_header;

  try {
    
    std::ifstream ifs(argv[1], std::ios::binary);
    if (!ifs) {
      throw std::runtime_error("Cannot open input file");
    }
    
    // method call to read header file for the input ppm image
    PPM_read_header(ifs, img_header);    
    std::cout << img_header << std::endl;  

    // declaring a pointer to an RGB_8 and using the new operator to allocate it
    // this pointer is used to store rgb value of each pixel in the image
    RGB_8 *img = new RGB_8[img_header.height * img_header.width];
    PPM_read_rgb_8(ifs, img_header.width, img_header.height, (RGB_8 *) img);
  
    std::ofstream ofs(argv[2], std::ios::binary);
  
    if (!ofs) {
      throw std::runtime_error("Cannot open output file");
    }
  
  	// This block deals with printing the execution time
    struct timeval tv1;
    struct timeval tv2;
    gettimeofday(&tv1, NULL);

    // method call to convert image into gray scale and 
	// flip image
	to_grayscale((RGB_8 *)img,  img_header.width, img_header.height);
	flip( img_header.width);

    gettimeofday(&tv2, NULL);
    std::cout << "Total time: " << (double) (tv2.tv_usec- tv1.tv_usec) / 1000000 +
    (double) (tv2.tv_sec - tv1.tv_sec) << std::endl;
	
	// write header and image
    PPM_write_header_8(ofs, img_header.width, img_header.height);
    PPM_write_rgb_8(ofs, img_header.width, img_header.height, (RGB_8 *) img);
 
    ifs.close();
    ofs.close();
    
  } catch (std::runtime_error &re) {
    std::cout << re.what() << std::endl;
    return 2;
  }
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
  //inp >> space;   // finish the header
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
    outp << PPM_MAGIC_1 << PPM_MAGIC_2 << (char) 10 << width << (char) 10 
       << height << (char) 10 << 255 << (char) 10;
}

// This method writes the image
void PPM_write_rgb_8(std::ofstream &outp, int width, int height, RGB_8 *img) {  
  outp.write((char *)img, sizeof(RGB_8) * width * height);
  if (!outp) {
    std::stringstream ss;
    ss << "error: only " << outp.tellp() << " could be written";
    throw std::runtime_error(ss.str());   
  }

}

// This method prints the dimension of the input image
std::ostream &operator<<(std::ostream &os, const PPM_header &header) {
	os << "Width: " << header.width << " Height: " << header.height << " Max color: " << header.max_color << std::endl;
	return os;
}

// This method converts the input image into gray scale
void to_grayscale(RGB_8 *img, int width, int height){

 for (int i=0; i<height;i++){

   for (int j=0;j<width;j++)
    {

     int temp = 0.21 * img[i*width+j].r + 0.72 * img[i*width+j].g + 0.07 *img[i*width+j].b;
     img[i*width+j].r= temp;
     img[i*width+j].g = temp;
     img[i*width+j].b = temp;
  
     }
 
    enqueue(pipeline,(RGB_8*) &img[i*width]);

  }

 enqueue(pipeline,(RGB_8*) 0 ); 

}

// This method flips the input image
void flip(int width) {

  RGB_8* img;
  int k=width;

  do{

     img=dequeue(pipeline);              // set row to dequeue(pipleline)

     if (img!=0)
      { 

       for (int row=0;row<width/2;row++)       //each rgb color value in row 
           std::swap(img[row],img[width-row-1]);
      }

   }  while(img!=0);
}

// This method returns a row from the queue
RGB_8 *dequeue(std::queue<RGB_8 *> &q) {

  RGB_8 *image_row;
  bool keep_checking = true;

  while (keep_checking) {
  
    //#pragma omp critical (pipeline)
       {
         if (!pipeline.empty()) {
           image_row = pipeline.front();
           pipeline.pop();
           keep_checking = false;
         }
       }
    //#pragma omp taskyield
 }

  return image_row;
    
}

// This function inserts a row in the queue 
void enqueue(std::queue<RGB_8 *> &q, RGB_8 *row) {
  //#pragma omp critical (pipeline) 

  {
    pipeline.push(row);

  }

}   