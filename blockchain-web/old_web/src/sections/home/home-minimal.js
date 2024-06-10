import { m } from 'framer-motion';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Stack from '@mui/material/Stack';
import { alpha } from '@mui/material/styles';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

import { varFade, MotionViewport } from 'src/components/animate';
import BackgroundView from '../_examples/extra/animate-view/background';

// ----------------------------------------------------------------------

const CARDS = [
  {
    icon: ' /assets/icons/home/ic_gold.png',
    title: 'Vàng 24K - Lựa chọn hoàn hảo cho sự sang trọng',
    description:
      'Khám phá bộ sưu tập vàng 24K của chúng tôi, nơi tinh hoa của nghệ thuật vàng hội tụ để tạo ra những sản phẩm đẳng cấp và sang trọng nhất.',
  },
  {
    icon: ' /assets/icons/home/ic_gold.png',
    title: 'Mang Đẳng Cấp vào Cuộc Sống với Vàng 18K',
    description:
      'Với chất lượng vàng 18K, chúng tôi tự hào giới thiệu những sản phẩm vàng đẳng cấp, từ nhẫn đến dây chuyền, làm nổi bật phong cách và sự thanh lịch trong mọi dịp.',
  },
  {
    icon: ' /assets/icons/home/ic_gold.png',
    title: 'Vàng Trắng và Vàng Hồng  Đẹp Tinh Khôi, Tinh Tế',
    description:
      'Khám phá sự thanh lịch của vàng trắng và sự ấm áp của vàng hồng trong các mẫu trang sức độc đáo của chúng tôi. Mỗi sản phẩm là một tuyệt tác đẹp tinh khôi và tinh tế.',
  },
];

// ----------------------------------------------------------------------

export default function HomeMinimal() {
  return (
    <Container
      component={MotionViewport}
      sx={{
        py: { xs: 10, md: 15 },
        backgroundColor: '#4CAF50',
      }}
    >
      <Stack
        spacing={3}
        sx={{
          textAlign: 'center',
          mb: { xs: 5, md: 10 },
        }}
      >
        <m.div variants={varFade().inUp}>
          <Typography
            component="div"
            variant="overline"
            sx={{ color: 'yellow', fontSize: '1.5rem' }}
          >
            BGSS
          </Typography>
        </m.div>

        <m.div variants={varFade().inDown}>
          <Typography variant="h2">
            What BGSS <br /> helps you?
          </Typography>
        </m.div>
      </Stack>

      <Box
        gap={{ xs: 3, lg: 10 }}
        display="grid"
        alignItems="center"
        gridTemplateColumns={{
          xs: 'repeat(1, 1fr)',
          md: 'repeat(3, 1fr)',
        }}
      >
        {CARDS.map((card, index) => (
          <m.div variants={varFade().inUp} key={card.title}>
            <Card
              sx={{
                textAlign: 'center',
                boxShadow: { md: 'none' },
                bgcolor: 'background.default',
                p: (theme) => theme.spacing(10, 5),
                ...(index === 1 && {
                  boxShadow: (theme) => ({
                    md: `-40px 40px 80px ${
                      theme.palette.mode === 'light'
                        ? alpha(theme.palette.grey[500], 0.16)
                        : alpha(theme.palette.common.black, 0.4)
                    }`,
                  }),
                }),
              }}
            >
              <Box
                component="img"
                src={card.icon}
                alt={card.title}
                sx={{ mx: 'auto', width: 48, height: 48 }}
              />

              <Typography variant="h5" sx={{ mt: 8, mb: 2 }}>
                {card.title}
              </Typography>

              <Typography sx={{ color: 'text.secondary' }}>{card.description}</Typography>
            </Card>
          </m.div>
        ))}
      </Box>
    </Container>
  );
}
