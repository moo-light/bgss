import { forwardRef } from 'react';

import { Link } from '@mui/material';

// ----------------------------------------------------------------------

const RouterLink = forwardRef(({ ...other }, ref) => <Link ref={ref} {...other} />);

export default RouterLink;
