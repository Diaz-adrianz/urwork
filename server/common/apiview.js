import moment from 'moment/moment.js';
import { handleErrors } from './helpers.js';

export default class ApiView {
	constructor(MODEL, filters = {}, query = {}, deselects = '') {
		this.MODEL = MODEL;
		this.modelName = MODEL.collection.collectionName.substring(0, MODEL.collection.collectionName.length - 1);
		this.filters = { ...filters, ...query };
		this.deselects =
			deselects +
			' ' +
			(query.deselects
				?.split(',')
				.map((d) => '-' + d)
				.join(' ') || '');
		this.population = [];
		this.limit = query.limit ? parseInt(query.limit) : 20;

		['deselect', 'page', 'start', 'end', 'search', 'limit'].forEach((key) => delete this.filters[key]);
	}

	addPopulate(path, select) {
		const obj = { path, select };

		if (!this.population.includes(obj)) this.population.push(obj);
	}

	async list(search = '', searchableFields = [], start = '', end = '', page = 1) {
		if (start || end) {
			this.filters['createdAt'] = {};

			if (start) this.filters['createdAt']['$gte'] = moment(start).format();
			if (end) this.filters['createdAt']['$lte'] = moment(end).format();
		}

		if (search) {
			let pattern = [];

			searchableFields.forEach((field) => {
				let obj = {};
				obj[field] = { $regex: search.toLowerCase(), $options: 'i' };
				pattern.push(obj);
			});

			this.filters['$or'] = pattern;
		}

		const data = await this.MODEL.paginate(this.filters, {
				select: this.deselects,
				offset: parseInt(page) - 1,
				limit: this.limit,
				sort: '-createdAt',
				populate: this.population.length ? this.population : '',
			}),
			docs = data.docs;

		// ['docs', 'limit', 'offset', 'page', 'hasPrevPage', 'hasNextPage', 'pagingCounter'].forEach(
		// 	(key) => delete data[key]
		// );
		['docs', 'offset', 'pagingCounter', 'hasPrevPage', 'hasNextPage'].forEach((key) => delete data[key]);

		data['data'] = docs;

		return {
			status: 200,
			msg: `${data.totalDocs} ${this.modelName}(s) found`,
			data,
		};
	}

	async detail() {
		const data = await this.MODEL.findOne(this.filters, this.deselects).populate(
			this.population.length ? this.population : ''
		);

		if (!data || data == null) throw { name: 'NotFound', lookup: this.modelName };

		return {
			status: 200,
			msg: `${this.modelName} found`,
			data: data,
		};
	}

	async create(payload = {}) {
		const data = await this.MODEL.create({
			...payload,
		});

		return {
			status: 200,
			msg: `New ${this.modelName} created`,
			data: {
				_id: data._id,
			},
		};
	}

	async update(payload = {}) {
		const data = await this.MODEL.findOneAndUpdate(this.filters, { ...payload });

		if (!data || data == null) throw { name: 'NotFound', lookup: this.modelName };

		return {
			status: 200,
			msg: `${this.modelName} updated`,
			data: {
				_id: data._id,
			},
		};
	}

	async delete() {
		const data = await this.MODEL.deleteMany(this.filters);

		if (!data.deletedCount) throw { name: 'NotFound', lookup: this.modelName };

		return {
			status: 204,
			msg: `${data.deletedCount} ${this.modelName} removed`,
			data: {},
		};
	}

	async exec(func) {
		try {
			return await func;
		} catch (error) {
			let err = handleErrors(error);

			return {
				status: err.status,
				msg: err.msg,
				data: {},
			};
		}
	}
}
