import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { ListUser, UpdateProfile, UserInfo } from './control.js';

const router = exp.Router();

router.get('/', protect, ListUser);
router.get('/my', protect, UserInfo);
router.put('/my', protect, UpdateProfile);

export default router;
