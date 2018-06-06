export class FilterExPrinter {
    private map(ex:any):Filter {
        if ((ex as Filter).$type === 'IntersectionFilter') {
            return new IntersectionFilter((ex as IntersectionFilter).filters.map(f=>this.map(f)))
        } else if ((ex as Filter).$type === 'UnionFilter') {
            return new UnionFilter((ex as UnionFilter).filters.map(f=>this.map(f)))
        } else if ((ex as Filter).$type === 'ServiceByHcPartyTagCodeDateFilter') {
            const f = ex as ServiceByHcPartyTagCodeDateFilter
            return new ServiceByHcPartyTagCodeDateFilter(f.codeType,f.codeCode,f.tagType,f.tagCode)
        }
        throw 'Unmappable filter '+ex
    }

    print(ex:JSON) {
        return this.map(ex).print()
    }
}

class Filter {
    $type:string
    constructor($type: string) {
        this.$type = $type;
    }

    print() {
        return ''
    }
}

class IntersectionFilter extends Filter {
    filters: Array<Filter>

    constructor(filters: Array<Filter>) {
        super('IntersectionFilter')
        this.filters = filters
    }

    add(f:Filter) {this.filters.push(f); return this;}

    print() {
        return this.filters.map(f=>`(${f.print()})`).join(' & ')
    }
}

class UnionFilter extends Filter {
    filters: Array<Filter>

    constructor(filters: Array<Filter>) {
        super('UnionFilter')
        this.filters = filters
    }

    print() {
        return this.filters.map(f=>`(${f.print()})`).join(' | ')
    }
}

class ComparisonFilter extends Filter {
    leftPart:string
    rightPart:string
    comparator:string

    constructor(leftPart: string, rightPart: string, comparator: string) {
        super('ComparisonFilter');
        this.leftPart = leftPart;
        this.rightPart = rightPart;
        this.comparator = comparator;
    }

    print() {
        return `"${this.leftPart}" ${this.comparator} "${this.rightPart}"`
    }
}

class ServiceByHcPartyTagCodeDateFilter extends Filter {
    codeType: string
    codeCode: string
    tagType: string
    tagCode: string

    constructor(codeType: string, codeCode: string, tagType: string, tagCode: string) {
        super('ServiceByHcPartyTagCodeDateFilter')
        this.codeType = codeType;
        this.codeCode = codeCode;
        this.tagType = tagType;
        this.tagCode = tagCode;
    }

    print() {
        const out = new IntersectionFilter([])
        if (this.codeType) { out.add(new ComparisonFilter(this.codeType,this.codeCode,'=='))}
        if (this.tagType) { out.add(new ComparisonFilter(':'+this.tagType,this.tagCode,'=='))}
        return out.print()
    }
}

