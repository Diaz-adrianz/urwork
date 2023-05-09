import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { CountNotif, ListNotifs, ReadNotif } from './control.js';

const router = exp.Router();

router.get('/', protect, ListNotifs);
router.get('/count', protect, CountNotif);
router.get('/:key/read', protect, ReadNotif);

export default router;
