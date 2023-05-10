import exp from 'express';
import { protect } from '../middlewares/auth.js';
import { fileValidationIsError, UPLOAD } from '../middlewares/memoryStorage.js';
import { IsAuthor, ProjectCheck } from '../middlewares/projects.js';
import {
	AddImages,
	CreateProj,
	DeleteProj,
	DetailProj,
	ListProj,
	RemoveImage,
	StarringProj,
	UpdateProj,
} from './control.js';

const router = exp.Router();

router.get('/', ListProj);
router.get('/my', protect, ListProj);
router.get('/ongoing', protect, ListProj);

router.get('/starit/:key/:option', protect, StarringProj);
router.get('/:key', protect, DetailProj);

router.post('/', protect, CreateProj);
router.put('/:key', protect, ProjectCheck, IsAuthor, UpdateProj);
router.delete('/:key', protect, ProjectCheck, IsAuthor, DeleteProj);

router.post('/:key/images', protect, ProjectCheck, IsAuthor, UPLOAD.single('image'), fileValidationIsError, AddImages);
router.put('/:key/images', protect, ProjectCheck, IsAuthor, RemoveImage);

export default router;
