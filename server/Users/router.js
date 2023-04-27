import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { ListUser, UserInfo } from './control.js';

const router = exp.Router();

router.get('/', protect, ListUser);
router.get('/my', protect, UserInfo);

export default router;
