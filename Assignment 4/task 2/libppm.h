#define PPM_H

#ifdef DEBUG
#define D(x) x
#else
#define D(x)
#endif

#define PPM_MAGIC_1 'P'
#define PPM_MAGIC_2 '6' 

struct PPM_header {
	int width;
	int height;
	int max_color;
}; 

typedef unsigned char uint8_t;

struct RGB_8 {
	uint8_t r;
	uint8_t g;
	uint8_t b;
} __attribute__((packed));

void PPM_read_header(std::ifstream &inp, PPM_header &ppm_header);
std::ostream &operator<<(std::ostream &os, const RGB_8 &rgb);
void PPM_read_rgb_8(std::ifstream &inp, int width, int height, RGB_8 *img);
void PPM_write_header_8(std::ofstream &outp, int width, int height);
void PPM_write_rgb_8(std::ofstream &outp, int width, int height, RGB_8 *img);
std::ostream &operator<<(std::ostream &os, const PPM_header &header);
void to_grayscale(RGB_8 *img, int width, int height);
void flip(int width);
void enqueue(std::queue<RGB_8 *> &q, RGB_8 *row);
RGB_8 *dequeue(std::queue<RGB_8 *> &q);