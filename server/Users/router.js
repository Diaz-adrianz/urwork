import exp from 'express';
import { protect } from '../middlewares/auth.js';
import UPLOAD from '../middlewares/memoryStorage.js';
import { ListUser, UpdatePhotoProfile, UpdateProfile, UserInfo } from './control.js';

const router = exp.Router();

router.get('/', protect, ListUser);
router.get('/my', protect, UserInfo);
router.put('/my', protect, UpdateProfile);

router.put('/my/photo', protect, UPLOAD.single('photo'), UpdatePhotoProfile);

export default router;
